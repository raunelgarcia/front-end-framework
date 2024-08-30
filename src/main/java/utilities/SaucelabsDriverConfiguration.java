package utilities;

import static org.apache.http.HttpStatus.SC_OK;
import static utilities.Constants.SAUCELABS_TESTS_URL;
import static utilities.DriverConfiguration.setURL;
import static utilities.LocalEnviroment.*;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import saucelabs.api.ApiUtils;
import saucelabs.api.Response;
import saucelabs.client.SauceLabsClient;
import saucelabs.dto.AppStorageItemMetadataResponse;
import saucelabs.dto.AppStorageResponse;
import saucelabs.service.SauceLabsService;

public class SaucelabsDriverConfiguration {

  private static final String USER = LocalEnviroment.getUser();
  private static final String ACCESS_TOKEN = LocalEnviroment.getAccessToken();
  private static final String AUTHORIZATION =
      Base64.getEncoder().encodeToString((USER + ":" + ACCESS_TOKEN).getBytes());

  public static String getSaucelabsAppId(
      String authorization, String appId, String kind, String version) {
    // Check that appId is not null
    if (appId.isBlank()) {
      throw new IllegalArgumentException("AppIdentifier is not set");
    }

    // Retrieve app storage response
    SauceLabsService sauceLabsService = new SauceLabsService(new SauceLabsClient());
    Response<AppStorageResponse> response =
        sauceLabsService.getV1StorageFiles(authorization, appId, kind.toLowerCase(), 10);
    ApiUtils.checkStatusCode(response.getStatus(), SC_OK);
    AppStorageResponse appStorageResponse = response.getPayload();
    System.out.println(appStorageResponse);

    // Filter the apps
    return appStorageResponse.getItems().stream()
        .filter(item -> isValidApp(item.getMetadata(), version))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Version not found: ".concat(version)))
        .getId();
  }

  // This function abstracts the logic of getting the latest version of the app independent of the
  // platform
  private static String getVersion(AppStorageItemMetadataResponse metadata) {
    String version;
    if (LocalEnviroment.isAndroid()) {
      version = metadata.getVersion();
    } else {
      version = metadata.getShort_version();
    }
    if (version == null) {
      String platform = LocalEnviroment.getPlatform();
      throw new IllegalArgumentException(
          "The app is not compatible with the set platform: ".concat(platform));
    }
    return version;
  }

  private static boolean isValidApp(AppStorageItemMetadataResponse metadata, String version) {
    // If the version is 'latest', no need to match a specific version
    boolean versionMatches =
        "latest".equalsIgnoreCase(version) || getVersion(metadata).equals(version);

    // For iOS, check if the app matches the local environment's simulator/virtual device setup
    boolean iosCheck =
        LocalEnviroment.isAndroid()
            || metadata.getIs_simulator() == LocalEnviroment.isVirtualDevice();

    // Both version and iOS check need to pass
    return versionMatches && iosCheck;
  }

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
    if (!FrontEndOperation.isNullOrEmpty(deviceName)) {
      caps.setCapability("appium:deviceName", deviceName);
    }
    if (!FrontEndOperation.isNullOrEmpty(platformVersion)) {
      caps.setCapability("appium:platformVersion", platformVersion);
    }
    caps.setCapability("appium:automationName", automationName);

    MutableCapabilities sauceOptions = new MutableCapabilities();
    sauceOptions.setCapability("appiumVersion", getAppiumVersion());
    sauceOptions.setCapability("username", getUser());
    sauceOptions.setCapability("accessKey", getAccessToken());
    sauceOptions.setCapability("build", "selenium-build-VNFHT");
    sauceOptions.setCapability("name", platformName + "_Test");

    caps.setCapability("sauce:options", sauceOptions);

    return caps;
  }

  public static IOSDriver configureSauceIOS() {
    String appStorage = null;

    try {
      appStorage =
          getSaucelabsAppId(AUTHORIZATION, getAppVersion(), getPlatform(), getAppIdentifier());
    } catch (Exception e) {
      e.printStackTrace();
    }

    MutableCapabilities caps =
        configureCommonCapabilities(
            "iOS", "storage:" + appStorage, getDeviceName(), getPlatformVersion(), "XCUITest");

    URL url = null;
    try {
      url = new URL(SAUCELABS_TESTS_URL);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
    return new IOSDriver(url, caps);
  }

  public static AndroidDriver configureSauceAndroid() {
    String appStorage = null;

    try {
      appStorage =
          getSaucelabsAppId(AUTHORIZATION, getAppVersion(), getPlatform(), getAppIdentifier());
    } catch (Exception e) {
      e.printStackTrace();
    }

    MutableCapabilities caps =
        configureCommonCapabilities(
            "Android",
            "storage:" + appStorage,
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
