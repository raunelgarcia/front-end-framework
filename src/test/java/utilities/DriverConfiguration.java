package utilities;

import io.appium.java_client.android.AndroidDriver;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
      WebDriver driver = configureWebDriver();
      Dimension windowResolution = ScreenResolution.getResolutionFromEnv();
      if (Objects.nonNull(windowResolution)) {
        driver.manage().window().setSize(windowResolution);
      }
      driver.get(LocalEnviroment.getUrl());
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
    Map<String, Map<String, String>> enviroment = loadCapabilities();
    Map<String, String> capabilitiesYaml = enviroment.get("capabilities");
    Map<String, String> capabilitiesApkYaml = enviroment.get("capabilitiesApk");

    MutableCapabilities capabilities = new DesiredCapabilities();
    for (Map.Entry<String, String> entry : capabilitiesYaml.entrySet()) {
      String capabilityName = entry.getKey();
      String capabilityValue = entry.getValue();
      if (Objects.nonNull(capabilityValue) && !capabilityValue.isEmpty()) {
        capabilities.setCapability(capabilityName, capabilityValue);
      }
    } if (Objects.isNull(capabilities.getCapability("apk"))) {
      for (Map.Entry<String, String> entry : capabilitiesApkYaml.entrySet()) {
        capabilities.setCapability(entry.getKey(), entry.getValue());
      }
    }
    capabilities.setCapability("platformName", LocalEnviroment.getPlatform());

    return capabilities;
  }

  public static Map<String, Map<String, String>> loadCapabilities() {
    Yaml yaml = new Yaml();
    try (InputStream inputStream =
                 DriverConfiguration.class
                         .getClassLoader()
                         .getResourceAsStream("yaml/capabilities.yaml")) {
      return yaml.load(inputStream);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load or parse the YAML file", e);
    }
  }
}
