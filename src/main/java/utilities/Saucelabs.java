package utilities;

import static utilities.DriverConfiguration.setURL;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

public class Saucelabs {
  public static WebDriver getSauceDriver() {
    Dimension windowResolution = ScreenResolution.getResolutionFromEnv();
    String url = setURL();
    WebDriver driver = configureSauceWeb();
    driver.manage().window().setSize(windowResolution);
    driver.manage().window().maximize();
    driver.get(url);
    return driver;
  }

  public static WebDriver configureSauceWeb() {
    Map<String, Object> sauceOptions = new HashMap<>();
    sauceOptions.put("username", "raulgalera97");
    sauceOptions.put("accessKey", "31551aa9-6e4e-4a62-b0b8-dcec2ddfac31");
    sauceOptions.put("build", "selenium-build-VNFHT");
    sauceOptions.put("name", "First Demo Test");
    URL url;
    try {
      url = new URL("https://ondemand.eu-central-1.saucelabs.com:443/wd/hub");
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
