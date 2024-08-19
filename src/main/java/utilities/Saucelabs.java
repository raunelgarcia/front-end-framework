package utilities;

import static utilities.Constants.SAUCELABS_TESTS_URL;
import static utilities.DriverConfiguration.setURL;
import static utilities.LocalEnviroment.getAccessToken;
import static utilities.LocalEnviroment.getDeviceName;
import static utilities.LocalEnviroment.getPlatformVersion;
import static utilities.LocalEnviroment.getUser;
import static utilities.LocalEnviroment.isAndroid;
import static utilities.LocalEnviroment.isIOS;
import static utilities.LocalEnviroment.isWeb;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

public class Saucelabs {
  public static WebDriver getSauceDriver() {
    if (isWeb()) {
      Dimension windowResolution = ScreenResolution.getResolutionFromEnv();
      String url = setURL();
      WebDriver driver = configureSauceWeb();
      driver.manage().window().setSize(windowResolution);
      driver.get(url);
      return driver;
    } else if (isAndroid()) {
      return configureSauceAndroid();
    } else if (isIOS()) {
      return configureSauceIOS();
    }
    return null;
  }

  public static MutableCapabilities configureCommonCapabilities(
      String platformName,
      String app,
      String deviceName,
      String platformVersion,
      String automationName) {
    MutableCapabilities caps = new MutableCapabilities();
    caps.setCapability("platformName", platformName);
    caps.setCapability("appium:app", app);
    if (deviceName != null) {
      caps.setCapability("appium:deviceName", deviceName);
    }
    if (platformVersion != null) {
      caps.setCapability("appium:platformVersion", platformVersion);
    }
    caps.setCapability("appium:automationName", automationName);

    MutableCapabilities sauceOptions = new MutableCapabilities();
    sauceOptions.setCapability("appiumVersion", "2.11.0");
    sauceOptions.setCapability("username", getUser());
    sauceOptions.setCapability("accessKey", getAccessToken());
    sauceOptions.setCapability("build", "selenium-build-VNFHT");
    sauceOptions.setCapability("name", platformName + "_Test");

    caps.setCapability("sauce:options", sauceOptions);

    return caps;
  }

  public static IOSDriver configureSauceIOS() {
    MutableCapabilities caps =
        configureCommonCapabilities(
            "iOS",
            "storage:filename="+System.getenv("App"),
            null,
            null,
            "XCUITest");

    URL url = null;
    try {
      url = new URL(SAUCELABS_TESTS_URL);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
    return new IOSDriver(url, caps);
  }

  public static AndroidDriver configureSauceAndroid() {
    MutableCapabilities caps =
        configureCommonCapabilities(
            "Android",
            "storage:filename="+System.getenv("App"),
            getDeviceName(),
            getPlatformVersion(),
            "UiAutomator2");

    URL url = null;
    try {
      url = new URL(SAUCELABS_TESTS_URL);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
    return new AndroidDriver(url, caps);
  }

  public static WebDriver configureSauceWeb() {
    Map<String, Object> sauceOptions = new HashMap<>();
    sauceOptions.put("username", getUser());
    sauceOptions.put("accessKey", getAccessToken());
    sauceOptions.put("build", "selenium-build-VNFHT");
    sauceOptions.put("name", "First Demo Test");
    sauceOptions.put("extendedDebugging", true);
    URL url;
    try {
      url = new URL(SAUCELABS_TESTS_URL);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
    String browser = LocalEnviroment.getBrowser();
    switch (browser) {
      case "edge":
        EdgeOptions edgeOptions = new EdgeOptions();
        edgeOptions.setPlatformName("Windows 11");
        edgeOptions.setBrowserVersion("latest");
        edgeOptions.setCapability("sauce:options", sauceOptions);
        return new RemoteWebDriver(url, edgeOptions);
      case "firefox":
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setPlatformName("Windows 11");
        firefoxOptions.setBrowserVersion("latest");
        firefoxOptions.setCapability("sauce:options", sauceOptions);
        return new RemoteWebDriver(url, firefoxOptions);
      default:
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setPlatformName("Windows 11");
        chromeOptions.setBrowserVersion("latest");
        chromeOptions.setCapability("sauce:options", sauceOptions);
        return new RemoteWebDriver(url, chromeOptions);
    }
  }
}
