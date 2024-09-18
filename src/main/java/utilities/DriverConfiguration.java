package utilities;

import static utilities.Constants.*;
import static utilities.FrontEndOperation.isNullOrEmpty;
import static utilities.LocalEnviroment.*;
import static utilities.SaucelabsDriverConfiguration.*;
import static utilities.ScreenResolution.getResolutionFromEnv;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import java.io.InputStream;
import java.net.URI;
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
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.yaml.snakeyaml.Yaml;

public class DriverConfiguration {
  public static String SLsession;
  private static WebDriver currentDriver;

  public static MutableCapabilities getMobileCapabilities() {
    MutableCapabilities caps = new MutableCapabilities();
    caps.setCapability("platformName", getPlatform());
    caps.setCapability("appium:automationName", isAndroid() ? "UiAutomator2" : "XCUITest");
    caps.setCapability("language", getLanguageCode());
    caps.setCapability(
        "locale", isAndroid() ? getCountryCode() : getLanguageCode() + "_" + getCountryCode());

    if (!isVirtualDevice()) {
      caps.setCapability("appium:newCommandTimeout", 90);
    }

    if (isSaucelabs()) {
      String appStorage =
          getSaucelabsAppId(AUTHORIZATION, getAppIdentifier(), getPlatform(), getAppVersion());
      caps.setCapability("appium:app", "storage:" + appStorage);
      caps.setCapability("appium:deviceName", getDeviceName());
      caps.setCapability("appium:platformVersion", getPlatformVersion());
      caps.setCapability("sauce:options", getSauceOptions());
    } else {
      caps.setCapability("udid", getUdid());
      caps.setCapability("noReset", true);
      if (isAndroid()) {
        caps.setCapability("appPackage", getAppIdentifier());
        String apk = getApp();
        if (!isNullOrEmpty(apk)) {
          caps.setCapability(
              "app", Paths.get(Constants.RESOURCE_PATH + apk).toAbsolutePath().toString());
        } else {
          caps.setCapability("appActivity", getAppActivity());
        }
      } else {
        caps.setCapability("bundleId", getAppIdentifier());
      }
    }

    return caps;
  }

  public static URL getDriverURL() {
    try {
      URI uri = new URI(isSaucelabs() ? SAUCELABS_TESTS_URL : DRIVER_URL);
      return uri.toURL();
    } catch (Exception e) {
      // Handle exception, such as MalformedURLException or URISyntaxException
      e.printStackTrace();
      return null;
    }
  }

  public static WebDriver getDriver() {
    if (Objects.nonNull(currentDriver)) {
      return currentDriver;
    }
    URL driverURL = Objects.requireNonNull(getDriverURL());
    if (isWeb()) {
      Dimension windowResolution = getResolutionFromEnv();
      String url = setURL();

      WebDriver driver = configureWebDriver();

      driver.manage().window().setSize(windowResolution);
      driver.get(url);
      currentDriver = driver;
    } else {

      MutableCapabilities caps = getMobileCapabilities();
      if (isAndroid()) {
        currentDriver = new AndroidDriver(driverURL, caps);
      } else {
        currentDriver = new IOSDriver(driverURL, caps);
      }
    }
    if (isSaucelabs()) {
      SLsession = getSauceLabsLink(currentDriver);
      Logger.infoMessage(
          "SauceLabs test session: ".concat(Constants.SAUCELABS_SESSION_URL).concat(SLsession));
    }
    return currentDriver;
  }

  public static void quitDriver() {
    currentDriver.quit();
    currentDriver = null;
  }

  private static WebDriver configureWebDriver() {
    WebDriver driver;
    String browser = getBrowser();
    String language = getLanguage();
    URL driverURL = Objects.requireNonNull(getDriverURL());

    switch (browser) {
      case "edge" -> {
        EdgeOptions edgeOptions = new EdgeOptions();
        edgeOptions.addArguments("--lang=" + language);
        if (isSaucelabs()) {
          setSauceWebCapabilities(edgeOptions);
          driver = new RemoteWebDriver(driverURL, edgeOptions);
        } else {
          driver = new EdgeDriver(edgeOptions);
        }
      }
      case "firefox" -> {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("intl.accept_languages", language);
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setProfile(profile);
        if (isSaucelabs()) {
          setSauceWebCapabilities(firefoxOptions);
          driver = new RemoteWebDriver(driverURL, firefoxOptions);
        } else {
          driver = new FirefoxDriver(firefoxOptions);
        }
      }
      case "safari" -> {
        SafariOptions safariOptions = new SafariOptions();
        if (isSaucelabs()) {
          setSauceWebCapabilities(safariOptions);
          driver = new RemoteWebDriver(driverURL, safariOptions);
        } else {
          driver = new SafariDriver(safariOptions);
        }
      }

      default -> {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--lang=" + language, "--disable-search-engine-choice-screen");
        if (isSaucelabs()) {
          setSauceWebCapabilities(chromeOptions);
          driver = new RemoteWebDriver(getDriverURL(), chromeOptions);
        } else {
          driver = new ChromeDriver(chromeOptions);
        }
      }
    }

    return driver;
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
    String base = getApplicationUrl();

    if (base.contains("{country}")) {
      String newUrl = base.replace("{country}", getLanguageCode());

      return newUrl;
    }

    return base;
  }

  public static String getSauceLabsLink(WebDriver driver) {
    return ((RemoteWebDriver) driver).getSessionId().toString();
  }
}
