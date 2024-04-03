package utilities;

import io.appium.java_client.android.AndroidDriver;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
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
      WebDriver driver = configureWebDriver();
      driver.manage().window().setSize(windowResolution);
      Instant startTime = Instant.now();
      driver.get(LocalEnviroment.getUrl());
      Instant endTime = Instant.now();
      Duration duration = Duration.between(startTime, endTime);
      System.out.println("PageLoad time: " + duration.toMillis() + " milliseconds");
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

  private static MutableCapabilities fillCapabilitiesAndroid() {
    Map<String, Map<String, String>> environment = loadCapabilitiesAndroid();
    Map<String, String> androidCapabilitiesYaml = environment.get("capabilities");

    MutableCapabilities capabilities = new DesiredCapabilities();
    for (Map.Entry<String, String> entry : androidCapabilitiesYaml.entrySet()) {
      String capabilityName = entry.getKey();
      String capabilityValue = entry.getValue();
      if (Objects.nonNull(capabilityValue) && !capabilityValue.isEmpty()) {
        capabilities.setCapability(capabilityName, capabilityValue);
      }
      capabilities.setCapability("platformName", LocalEnviroment.getPlatform());
      capabilities.setCapability("udid", LocalEnviroment.getUdid());
      capabilities.setCapability("apk", LocalEnviroment.getApk());
      capabilities.setCapability("appActivity", LocalEnviroment.getAppActivity());
      capabilities.setCapability("appPackage", LocalEnviroment.getAppPackage());
    }

    return capabilities;
  }

  public static Map<String, Map<String, String>> loadCapabilitiesAndroid() {
    Yaml yaml = new Yaml();
    try (InputStream inputStream =
                 DriverConfiguration.class
                         .getClassLoader()
                         .getResourceAsStream("yaml/mobileConfiguration.yaml")) {
      return yaml.load(inputStream);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load or parse the YAML file", e);
    }
  }
}
