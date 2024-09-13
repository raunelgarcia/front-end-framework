package utilities;

import static utilities.Constants.DRIVER_URL;
import static utilities.LocalEnviroment.*;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.yaml.snakeyaml.Yaml;
import saucelabs.service.SauceLabsService;
import saucelabs.client.SauceLabsClient;
import utilities.*;

public class DriverConfiguration {
  private static WebDriver currentDriver;
  private static SauceLabsClient client = new SauceLabsClient();
  private static SauceLabsService service = new SauceLabsService(client);

  public static WebDriver getDriver() {
    if (currentDriver != null) {
      return currentDriver;
    }
    if (Objects.nonNull(getProvider()) && getProvider().equalsIgnoreCase("SauceLabs")) {
      currentDriver = SaucelabsDriverConfiguration.getSauceDriver();
      showSauceLabsLink(currentDriver);
    } else {
      if (isWeb()) {
        Dimension windowResolution = ScreenResolution.getResolutionFromEnv();
        String url = setURL();

        WebDriver driver = configureWebDriver();
        driver.manage().window().setSize(windowResolution);
        driver.get(url);
        currentDriver = driver;
      } else if (isAndroid()) {
        try {
          URL url = new URL(DRIVER_URL);
          currentDriver = new AndroidDriver(url, fillCapabilities());
        } catch (MalformedURLException e) {
          throw new RuntimeException(e);
        }
      } else {
        try {
          URL url = new URL(DRIVER_URL);
          currentDriver = new IOSDriver(url, fillCapabilities());

        } catch (MalformedURLException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return currentDriver;
  }

  public static void quitDriver() {
    currentDriver.quit();
    currentDriver = null;
  }

  private static WebDriver configureWebDriver() {
    WebDriver driver;
    String browser = LocalEnviroment.getBrowser();
    switch (browser) {
      case "edge" -> {
        EdgeOptions edgeOptions = new EdgeOptions();
        edgeOptions.addArguments("--lang=" + LocalEnviroment.getLanguage());
        driver = new EdgeDriver(edgeOptions);
      }
      case "firefox" -> {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("intl.accept_languages", LocalEnviroment.getLanguage());
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setProfile(profile);
        driver = new FirefoxDriver(firefoxOptions);
      }
      case "safari" -> {
        // No es común definir la versión de Safari, pero puedes adaptar si es necesario.
        driver = new SafariDriver();
      }
      default -> {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--lang=" + LocalEnviroment.getLanguage());
        chromeOptions.addArguments("--disable-search-engine-choice-screen");
        driver = new ChromeDriver(chromeOptions);
      }
    }

    return driver;
  }

  private static MutableCapabilities fillCapabilities() throws IllegalArgumentException {
    Map<String, Map<String, String>> environment;
    Map<String, String> capabilities;
    MutableCapabilities filledCapabilities = new DesiredCapabilities();

    filledCapabilities.setCapability("platformName", LocalEnviroment.getPlatform());
    filledCapabilities.setCapability("udid", LocalEnviroment.getUdid());
    if (isAndroid()) {
      environment = loadCapabilitiesMobile(Constants.ANDROID_CONFIG);
      capabilities = environment.get("capabilitiesAndroid");
      filledCapabilities.setCapability("appPackage", LocalEnviroment.getAppIdentifier());
      filledCapabilities.setCapability("language", LocalEnviroment.getLanguageCode());
      filledCapabilities.setCapability("locale", LocalEnviroment.getCountryCode());
      String apk = LocalEnviroment.getApp();
      if (Objects.nonNull(apk) && !apk.isEmpty()) {
        filledCapabilities.setCapability(
            "app", Paths.get(Constants.RESOURCE_PATH + apk).toAbsolutePath().toString());
      } else {
        filledCapabilities.setCapability("appActivity", LocalEnviroment.getAppActivity());
      }
    } else {
      environment = loadCapabilitiesMobile(Constants.IOS_CONFIG);
      capabilities = environment.get("capabilitiesiOS");
      filledCapabilities.setCapability("bundleId", LocalEnviroment.getAppIdentifier());
    }

    if (Objects.isNull(capabilities)) {
      throw new IllegalArgumentException("Capabilities are not set");
    }

    for (Map.Entry<String, String> entry : capabilities.entrySet()) {
      String capabilityName = entry.getKey();
      String capabilityValue = entry.getValue();
      if (Objects.nonNull(capabilityValue) && !capabilityValue.isEmpty()) {
        filledCapabilities.setCapability(capabilityName, capabilityValue);
      } else {
        throw new IllegalArgumentException("Capabilities cannot be blank");
      }
    }

    return filledCapabilities;
  }

  public static Map<String, Map<String, String>> loadCapabilitiesMobile(String path) {
    Yaml yaml = new Yaml();
    try (InputStream inputStream =
        DriverConfiguration.class.getClassLoader().getResourceAsStream(path)) {
      return yaml.load(inputStream);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load or parse the YAML file", e);
    }
  }

  public static Map<String, Map<String, String>> loadCapabilitiesWeb() {
    Yaml yaml = new Yaml();
    try (InputStream inputStream =
        DriverConfiguration.class.getClassLoader().getResourceAsStream(Constants.WEB_CONFIG)) {
      return yaml.load(inputStream);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load or parse the YAML file", e);
    }
  }

  public static String setURL() {
    String base = LocalEnviroment.getApplicationUrl();

    if (base.contains("{country}")) {
      String newUrl = base.replace("{country}", LocalEnviroment.getLanguageCode());

      return newUrl;
    }

    return base;
  }

  public static void showSauceLabsLink(WebDriver driver) {
    String sessionId = ((RemoteWebDriver) driver).getSessionId().toString();

    Logger.infoMessage(
        "SauceLabs test session: ".concat(Constants.SAUCELABS_SESSION_URL).concat(sessionId));
  }
}
