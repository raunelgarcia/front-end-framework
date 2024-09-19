package utilities;

import static j2html.TagCreator.*;
import static utilities.Constants.SAUCELABS_SESSION_URL;
import static utilities.DriverConfiguration.*;

import com.google.common.collect.ImmutableMap;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import j2html.tags.UnescapedText;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AllureReport {
  private static final RemoteWebDriver driver = (RemoteWebDriver) getDriver();
  private static final Capabilities capabilities = driver.getCapabilities();
  private static String descriptionHtml = "";
  private static UnescapedText checks;

  public static void fillReportInfo() {
    descriptionHtml = setTestDescription();
    Allure.descriptionHtml(descriptionHtml);
    descriptionHtml = "";
    allureEnvironmentWriter(setAllureParameters());
  }

  private static String setTestDescription() {
    String platformName = capabilities.getCapability("platformName").toString();
    String language = LocalEnviroment.getLanguage();

    UnescapedText description =
        join(
            h3("Test Enviroment").withStyle("text-decoration: underline;"),
            p(b("Platform: "), text(platformName)),
            p(b("Language: "), text(language)));

    if (LocalEnviroment.isMobile()) {
      String platformVersion = (String) capabilities.getCapability("appium:platformVersion");
      description =
          join(
              description,
              p(b("Device Name: "), text(getDeviceName())),
              p(b("Platform Version: "), text(platformVersion)),
              p(b("Udid: "), text(getUdid())));

      String appActivity = (String) capabilities.getCapability("appium:appActivity");
      if (!FrontEndOperation.isNullOrEmpty(appActivity)) {
        description = join(description, p(b("App Activity: "), text(appActivity)));
      }

      String appIdentifier = getAppIdentifier();
      if (!FrontEndOperation.isNullOrEmpty(appIdentifier)) {
        description = join(description, p(b("App Identifier: "), text(appIdentifier)));
      }

      if (!FrontEndOperation.isNullOrEmpty(getApp())) {
        description = join(description, p(b("App: "), text(getApp())));
      }
    } else {
      description =
          join(
              description,
              p(b("Browser: "), text((String) capabilities.getCapability("browserName"))),
              p(b("Url: "), text(driver.getCurrentUrl())),
              p(b("Resolution: "), text(driver.manage().window().getSize().toString())),
              p(b("Accessibility: "), text(String.valueOf(LocalEnviroment.getAccessibility()))),
              p(b("Operating System: "), text(platformName)));
    }

    // Append additional checks HTML if any
    description = join(description, checks);
    checks = new UnescapedText(""); // Clear checks after appending

    return description.render(); // Render the complete HTML in one call
  }

  private static String getUdid() {
    return (String)
        capabilities.getCapability(
            capabilities.getCapabilityNames().contains("appium:udid")
                ? "appium:udid"
                : "appium:deviceUDID");
  }

  private static String getAppIdentifier() {
    String appIdentifier =
        (String)
            capabilities.getCapability(
                capabilities.getCapabilityNames().contains("appium:appPackage")
                    ? "appium:appPackage"
                    : "appium:bundleId");
    return FrontEndOperation.isNullOrEmpty(appIdentifier)
        ? LocalEnviroment.getAppIdentifier()
        : appIdentifier;
  }

  private static String getApp() {
    String app = (String) capabilities.getCapability("appium:app");
    return FrontEndOperation.isNullOrEmpty(app) ? "" : app.substring(app.lastIndexOf("/") + 1);
  }

  private static String getDeviceName() {
    if (LocalEnviroment.isSaucelabs()) {
      if (LocalEnviroment.isVirtualDevice()) {
        return (String) capabilities.getCapability("appium:deviceName");
      }
      return (String) capabilities.getCapability("appium:testobject_device_name");
    } else {
      return (String) capabilities.getCapability("appium:deviceModel");
    }
  }

  public static void addComparation(String comparationMessage, boolean success) {
    checks =
        join(
            checks,
            h4(comparationMessage)
                .withStyle(
                    (success ? "background-color: #97cc64;" : "background-color: #fd5a3e;")
                        + " padding: 8px; color: #fff;"));
  }

  public static void attachTextFileToAllureReport(File file) {
    try {
      byte[] content = Files.readAllBytes(Paths.get(file.toURI()));
      Allure.addAttachment("NetworkLogs", "text/plain", new String(content));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void attachScreenshot(WebDriver driver) {
    if (driver instanceof TakesScreenshot) {
      byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
      Allure.getLifecycle().addAttachment("Screenshot", "image/png", "png", screenshot);
      Allure.getLifecycle().updateTestCase(testResult -> testResult.setStatus(Status.FAILED));
    }
  }

  public static void allureEnvironmentWriter(ImmutableMap<String, String> environmentValuesSet) {
    try {
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      Document doc = docBuilder.newDocument();
      Element environment = doc.createElement("environment");
      doc.appendChild(environment);
      environmentValuesSet.forEach(
          (k, v) -> {
            Element parameter = doc.createElement("parameter");
            Element key = doc.createElement("key");
            Element value = doc.createElement("value");
            key.appendChild(doc.createTextNode(k));
            value.appendChild(doc.createTextNode(v));
            parameter.appendChild(key);
            parameter.appendChild(value);
            environment.appendChild(parameter);
          });

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      File allureResultsDir = new File(System.getProperty("user.dir") + "/target/allure-results");
      if (!allureResultsDir.exists()) allureResultsDir.mkdirs();
      StreamResult result =
          new StreamResult(
              new File(System.getProperty("user.dir") + "/target/allure-results/environment.xml"));
      transformer.transform(source, result);
      Logger.infoMessage("Allure environment data saved.");
    } catch (ParserConfigurationException pce) {
      pce.printStackTrace();
    } catch (TransformerException tfe) {
      tfe.printStackTrace();
    }
  }

  public static ImmutableMap<String, String> setAllureParameters() {
    ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
    builder.put("Accessibility", String.valueOf(LocalEnviroment.getAccessibility()));
    String platformName = capabilities.getCapability("platformName").toString();
    builder.put("Platform", platformName);
    builder.put("Provider", LocalEnviroment.getProvider());

    if (LocalEnviroment.isSaucelabs()) {
      addSauceLabsParameters(builder);
    } else {
      switch (platformName.toLowerCase()) {
        case "android" -> addAndroidParameters(builder);

        case "ios" -> addIosParameters(builder);

        default -> addWebParameters(builder);
      }
    }
    return builder.build();
  }

  public static void addWebParameters(ImmutableMap.Builder<String, String> builder) {
    builder
        .put("Application", DriverConfiguration.setURL())
        .put(
            "Browser",
            capabilities
                .getCapability("browserName")
                .toString()
                .concat(
                    " ("
                        .concat(
                            ((HasCapabilities) getDriver()).getCapabilities().getBrowserVersion())
                        .concat(")")))
        .put("Resolution", driver.manage().window().getSize().toString());
  }

  public static void addSauceLabsParameters(ImmutableMap.Builder<String, String> builder) {
    builder.put("SauceLabs test session", SAUCELABS_SESSION_URL.concat(SLsession));
    if (LocalEnviroment.isMobile()) {
      if (!FrontEndOperation.isNullOrEmpty(getApp())) {
        builder.put("App", getApp());
      }
      builder
          .put("AppIdentifier", getAppIdentifier())
          .put("AppVersion", SaucelabsDriverConfiguration.appVersion)
          .put("DeviceName", getDeviceName())
          .put(
              "PlatformVersion",
              (String) driver.getCapabilities().getCapability("appium:platformVersion"))
          .put("Udid", getUdid());
    } else {
      addWebParameters(builder);
    }
  }

  public static void addAndroidParameters(ImmutableMap.Builder<String, String> builder) {
    builder.put("Udid", getUdid());
    if (FrontEndOperation.isNullOrEmpty(getApp())) {
      String appActivity = (String) capabilities.getCapability("appium:appActivity");
      builder.put("AppActivity", appActivity).put("AppIdentifier", getAppIdentifier());
    } else {
      builder.put("App", getApp());
    }
  }

  public static void addIosParameters(ImmutableMap.Builder<String, String> builder) {
    builder.put("Udid", getUdid());
    builder.put("AppIdentifier", getAppIdentifier());
  }
}
