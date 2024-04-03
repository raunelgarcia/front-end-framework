package utilities;

import static org.openqa.selenium.remote.CapabilityType.PLATFORM_NAME;

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
        return new AndroidDriver(url, fillCapabilities());

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

  private MutableCapabilities fillCapabilities() {
    MutableCapabilities capabilities = new DesiredCapabilities();
    capabilities.setCapability(PLATFORM_NAME, LocalEnviroment.getPlatform());
    capabilities.setCapability("automationName", "UiAutomator2");
    capabilities.setCapability("udid", LocalEnviroment.getUdid());
    capabilities.setCapability("noReset", true);
    String apk = LocalEnviroment.getApk();
    if (Objects.nonNull(apk) && !apk.isEmpty()) {
      capabilities.setCapability(
          "app", Paths.get("src/test/resources/" + apk).toAbsolutePath().toString());
    } else {
      capabilities.setCapability("appPackage", LocalEnviroment.getAppPackage());
      capabilities.setCapability("appActivity", LocalEnviroment.getAppActivity());
    }

    return capabilities;
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
