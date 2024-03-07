package utilities;

import static org.openqa.selenium.remote.CapabilityType.PLATFORM_NAME;

import io.appium.java_client.android.AndroidDriver;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Objects;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class DriverConfiguration {
  public WebDriver getDriver() {
    if (Objects.equals(LocalEnviroment.getPlatform(), "Web")) {
      WebDriver driver = congifureWebDriver();
      //driver.manage().window().maximize();
      driver.get(LocalEnviroment.getUrl());
      return driver;
    } else if (Objects.equals(LocalEnviroment.getPlatform(), "Android")) {
      try {
        URL url = new URL("http://127.0.0.1:4723");
        return new AndroidDriver(url, fillCapabilities());

      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }
    } else return null;
  }

  private WebDriver congifureWebDriver() {
    WebDriver driver = null;
    if (Objects.equals(LocalEnviroment.getBrowser(), "edge")) {
      driver = new EdgeDriver();
    } else if (Objects.equals(LocalEnviroment.getBrowser(), "firefox")) {
      driver = new FirefoxDriver();
    } else {
      driver = new ChromeDriver();
    }
    return driver;
  }

  private MutableCapabilities fillCapabilities() {
    MutableCapabilities capabilities = new DesiredCapabilities();
    capabilities.setCapability(PLATFORM_NAME, "Android");
    capabilities.setCapability("automationName", "UiAutomator2");
    capabilities.setCapability("udid", LocalEnviroment.getUdid());
    capabilities.setCapability("noReset", true);
    String apk = LocalEnviroment.getApk();
    if (apk != null && !apk.isEmpty()) {
      capabilities.setCapability(
          "app", Paths.get("src/test/resources/" + apk).toAbsolutePath().toString());
    } else {
      capabilities.setCapability("appPackage", LocalEnviroment.getAppPackage());
      capabilities.setCapability("appActivity", LocalEnviroment.getAppActivity());
    }

    return capabilities;
  }
}
