package utilities;


import io.appium.java_client.android.AndroidDriver;

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
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.yaml.snakeyaml.Yaml;

public class DriverConfiguration {

  public WebDriver getDriver() {
    if (LocalEnviroment.getPlatform().equalsIgnoreCase("Web")) {
      Dimension windowResolution = ScreenResolution.getResolutionFromEnv();
      String url = LocalEnviroment.getApplicationUrl();
      WebDriver driver = configureWebDriver();
      driver.manage().window().setSize(windowResolution);
      driver.get(url);
      return driver;
    } else if (LocalEnviroment.getPlatform().equalsIgnoreCase("Android")) {
      try {
        URL url = new URL("http://127.0.0.1:4723");
        return new AndroidDriver(url, fillCapabilitiesAndroid());

      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }
    } else {
      return null;
    }
  }

  private WebDriver configureWebDriver() {
    WebDriver driver;
    String browser = LocalEnviroment.getBrowser();

    switch (browser) {
      case "edge":
        driver = new EdgeDriver();
        break;
      case "firefox":
        driver = new FirefoxDriver();
        break;
      default:
        driver = new ChromeDriver();
        break;
    }
    return driver;
  }

  private static MutableCapabilities fillCapabilitiesAndroid() throws IllegalArgumentException{
    String platform = LocalEnviroment.getPlatform();
    Map<String, Map<String, String>> environment;
    Map<String, String> capabilities = null;
    MutableCapabilities filledCapabilities = new DesiredCapabilities();

    if (Objects.equals(platform, "Android")) {
      environment = loadCapabilitiesMobile(Constants.ANDROID_CONFIG);
      capabilities = environment.get("capabilitiesAndroid");
      filledCapabilities.setCapability("platformName", LocalEnviroment.getPlatform());
      filledCapabilities.setCapability("udid", LocalEnviroment.getUdid());
      String apk = LocalEnviroment.getApk();
      if (Objects.nonNull(apk) && !apk.isEmpty()) {
        filledCapabilities.setCapability("app", Paths.get(Constants.RESOURCE_PATH + apk).toAbsolutePath().toString());
      } else {
        filledCapabilities.setCapability("appPackage", LocalEnviroment.getAppPackage());
        filledCapabilities.setCapability("appActivity", LocalEnviroment.getAppActivity());
      }
    } else if (Objects.equals(platform, "iOS")) {
      environment = loadCapabilitiesMobile(Constants.IOS_CONFIG);
      capabilities = environment.get("capabilitiesiOS");
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
                 DriverConfiguration.class
                         .getClassLoader()
                         .getResourceAsStream(path)) {
      return yaml.load(inputStream);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load or parse the YAML file", e);
    }
  }

  public static Map<String, Map<String, String>> loadCapabilitiesWeb() {
    Yaml yaml = new Yaml();
    try (InputStream inputStream =
                 DriverConfiguration.class
                         .getClassLoader()
                         .getResourceAsStream("yaml/webConfiguration.yaml")) {
      return yaml.load(inputStream);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load or parse the YAML file", e);
    }
  }
}
