package utilities;

import static utilities.Constants.SAUCELABS_SESSION_URL;
import static utilities.DriverConfiguration.*;
import static utilities.LocalEnviroment.*;

import com.google.common.collect.ImmutableMap;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AllureReport {
  private static String descriptionHtml = "";
  private static String checksHtml = "";

  public static void fillReportInfo() {
    descriptionHtml = setTestDescription();
    Allure.descriptionHtml(descriptionHtml);
    descriptionHtml = "";
    allureEnvironmentWriter(setAllureParameters());
  }

  private static String setTestDescription() {
    WebDriver driver = getDriver();
    StringBuilder description = new StringBuilder();
    AppiumDriver driverMobile;
    String os = null;
    description.append("<h3 style=\"text-decoration: underline;\">Test Enviroment</h3>");
    description.append("<p><b>Platform:</b> ").append(LocalEnviroment.getPlatform()).append("</p>");
    description.append("<p><b>Language:</b> ").append(LocalEnviroment.getLanguage()).append("</p>");
    if (LocalEnviroment.isMobile()) {
      if (LocalEnviroment.isAndroid()) {
        driverMobile = (AndroidDriver) driver;
        String appActivity = LocalEnviroment.getAppActivity();
        String deviceName = driverMobile.getCapabilities().getCapability("deviceName").toString();
        description.append("<p><b>Device Name:</b> ").append(deviceName).append("</p>");
        String platformVersion =
            driverMobile.getCapabilities().getCapability("platformVersion").toString();

        description
            .append("<p><b>Platform Version:</b>".concat(isAndroid() ? "Android " : "IOS"))
            .append(platformVersion)
            .append("</p>");
        if (!FrontEndOperation.isNullOrEmpty(appActivity)) {
          description.append("<p><b>App Activity:</b> ").append(appActivity).append("</p>");
        }
      } else {
        driverMobile = (IOSDriver) driver;
      }
      description
          .append("<p><b>Udid:</b> ")
          .append(
              driverMobile
                  .getCapabilities()
                  .getCapability(isAndroid() ? "deviceUDID" : "udid")
                  .toString())
          .append("</p>");
      description
          .append("<p><b>App Identifier:</b> ")
          .append(LocalEnviroment.getAppIdentifier())
          .append("</p>");
      String apk = LocalEnviroment.getApp();
      if (!FrontEndOperation.isNullOrEmpty(apk)) {
        description.append("<p><b>App:</b> ").append(apk).append("</p>");
      }
    } else {
      if (LocalEnviroment.isWindows()) {
        os = "Windows";
      } else if (LocalEnviroment.isMac()) {
        os = "Mac";
      } else {
        os = "linux";
      }
      description.append("<p><b>Browser:</b> ").append(LocalEnviroment.getBrowser()).append("</p>");
      description
          .append("<p><b>Url:</b> ")
          .append(LocalEnviroment.getApplicationUrl())
          .append("</p>");
      description
          .append("<p><b>Resolution:</b> ")
          .append(ScreenResolution.getResolutionFromEnv())
          .append("</p>");
      description
          .append("<p><b>Accessibility:</b> ")
          .append(LocalEnviroment.getAccessibility())
          .append("</p>");
      description.append("<p><b>Operating System:</b> ").append(os).append("</p>");
    }
    description.append(checksHtml);
    checksHtml = "";
    return description.toString();
  }

  public static void addComparation(String comparationMessage, boolean success) {
    if (success)
      checksHtml =
          checksHtml.concat(
              "<h4 style=\"background-color: #97cc64; padding: 8px; color: #fff;\">"
                  + comparationMessage
                  + "</h4>");
    else
      checksHtml =
          checksHtml.concat(
              "<h4 style=\"background-color: #fd5a3e; padding: 8px; color: #fff;\">"
                  + comparationMessage
                  + "</h4>");
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

  public static void allureEnvironmentWriter(ImmutableMap<String, String> environmentValuesSet)  {
    try {
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      Document doc = docBuilder.newDocument();
      Element environment = doc.createElement("environment");
      doc.appendChild(environment);
      environmentValuesSet.forEach((k, v) -> {
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
      File allureResultsDir = new File( System.getProperty("user.dir")
              + "/target/allure-results");
      if (!allureResultsDir.exists()) allureResultsDir.mkdirs();
      StreamResult result = new StreamResult(
              new File( System.getProperty("user.dir")
                      + "/target/allure-results/environment.xml"));
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
    builder.put("Platform", LocalEnviroment.getPlatform());
    builder.put("Provider", LocalEnviroment.getProvider());

    if (LocalEnviroment.isSauceLabs()) {
      addSauceLabsParameters(builder);
    } else {
      builder.put("Udid", LocalEnviroment.getUdid());
      switch (LocalEnviroment.getPlatform().toLowerCase()) {
        case "android" -> addAndroidParameters(builder);

        case "ios" -> addIosParameters(builder);

        case "web" -> addWebParameters(builder);
      }
    }
    return builder.build();
  }

  public static void addWebParameters(ImmutableMap.Builder<String, String> builder) {
    builder.put("Application", DriverConfiguration.setURL())
    .put("Browser", LocalEnviroment.getBrowser().concat(" (".concat(((HasCapabilities) getDriver()).getCapabilities().getBrowserVersion()).concat(")")))
    .put("Resolution", LocalEnviroment.getResolution());
  }

  public static void addSauceLabsParameters(ImmutableMap.Builder<String, String> builder) {
    builder.put("SauceLabs test session", SAUCELABS_SESSION_URL.concat(SLsession));
    if (LocalEnviroment.isMobile()) {
      AppiumDriver driverMobile = (AppiumDriver) getDriver();
      builder.put("AppIdentifier", LocalEnviroment.getAppIdentifier())
      .put("AppVersion", SaucelabsDriverConfiguration.appVersion)
      .put("DeviceName", LocalEnviroment.getDeviceName())
      .put("PlatformVersion", driverMobile.getCapabilities().getCapability("platformVersion").toString());
    }else{
      addWebParameters(builder);
    }
  }

  public static void addAndroidParameters(ImmutableMap.Builder<String, String> builder) {
    if (FrontEndOperation.isNullOrEmpty(LocalEnviroment.getApp())) {
      builder.put("AppActivity", LocalEnviroment.getAppActivity())
      .put("AppIdentifier", LocalEnviroment.getAppIdentifier());
    } else {
      builder.put("App", LocalEnviroment.getApp());
    }
  }

  public static void addIosParameters(ImmutableMap.Builder<String, String> builder) {
    builder.put("AppIdentifier", LocalEnviroment.getAppIdentifier());
  }
}


