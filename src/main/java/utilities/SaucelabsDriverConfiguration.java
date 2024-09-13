package utilities;

import static org.apache.http.HttpStatus.SC_OK;
import static utilities.Constants.AUTHORIZATION;
import static utilities.Constants.SAUCELABS_TESTS_URL;
import static utilities.DriverConfiguration.setURL;
import static utilities.LocalEnviroment.*;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

import java.lang.Runtime.Version;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
import saucelabs.dto.AppBrowserVersion;
import saucelabs.dto.AppStorageItemMetadataResponse;
import saucelabs.dto.AppStorageResponse;
import saucelabs.service.SauceLabsService;

public class SaucelabsDriverConfiguration {

  /**
   * Retrieves the Sauce Labs app ID for a given app based on the specified version and kind.
   *
   * <p>This method fetches the app's storage files from Sauce Labs, filters the results to find the
   * correct version, and returns the corresponding app ID.
   *
   * <p>If the provided app ID is null or blank, or if the specified version is not found, it throws
   * an {@link IllegalArgumentException}.
   *
   * @param authorization The authorization token required to access the Sauce Labs API.
   * @param appId The identifier of the app to retrieve. Must be non-null and non-blank.
   * @param kind The type of the app (e.g., Android, iOS). This is converted to lowercase for the
   *     API call.
   * @param version The version of the app to retrieve. If "latest", retrieve tha last uploaded one.
   * @return The Sauce Labs app ID corresponding to the specified version.
   * @throws IllegalArgumentException If the app ID is null, blank, or if the specified version is
   *     not found.
   */
  public static String getSaucelabsAppId(
      String authorization, String appId, String kind, String version) {

    Optional.ofNullable(appId)
        .filter(id -> !id.isBlank())
        .orElseThrow(
            () -> new IllegalArgumentException("AppIdentifier must be not null and not empty"));

    SauceLabsService sauceLabsService = new SauceLabsService(new SauceLabsClient());
    Response<AppStorageResponse> response =
        sauceLabsService.getV1StorageFiles(authorization, appId, kind.toLowerCase(), 10);
    ApiUtils.checkStatusCode(response.getStatus(), SC_OK);

    return response.getPayload().getItems().stream()
        .filter(item -> isValidApp(item.getMetadata(), version))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Version not found: ".concat(version)))
        .getId();
  }

  /**
   * Retrieves the appropriate version of the app based on the platform.
   *
   * <p>This method abstracts the logic of obtaining the version of the app, making it independent
   * of the platform (Android or iOS). It returns the full version for Android and the short version
   * for iOS.
   *
   * <p>If the version is {@code null}, the method throws a {@link NullPointerException} indicating
   * that the app is not compatible with the current platform.
   *
   * @param metadata The metadata of the app, containing version details specific to each platform.
   * @return The version string corresponding to the app's platform.
   * @throws NullPointerException if the version is {@code null}, indicating incompatibility with
   *     the current platform.
   */
  private static String getVersion(AppStorageItemMetadataResponse metadata) {
    String version = isAndroid() ? metadata.getVersion() : metadata.getShort_version();
    return Objects.requireNonNull(
        version, "The app is not compatible with the set platform: ".concat(getPlatform()));
  }

  /**
   * Determines if an application is valid based on its metadata and the specified version.
   *
   * <p>This method checks two main conditions:
   *
   * <ul>
   *   <li>If the specified version is "latest", it will always return true for the version check.
   *       Otherwise, it verifies if the version matches the one in the metadata.
   *   <li>For iOS applications, it checks if the app matches the local environment's simulator or
   *       virtual device setup. This check is bypassed if the environment is Android.
   * </ul>
   *
   * @param metadata The metadata of the app, containing details such as version and platform
   *     information.
   * @param version The version of the app to validate against. If "latest", the version check will
   *     pass automatically.
   * @return {@code true} if both the version matches and the iOS compatibility check passes, {@code
   *     false} otherwise.
   */
  private static boolean isValidApp(AppStorageItemMetadataResponse metadata, String version) {
    boolean versionMatches =
        "latest".equalsIgnoreCase(version) || getVersion(metadata).equals(version);

    boolean iosCheck =
        LocalEnviroment.isAndroid()
            || metadata.getIs_simulator() == LocalEnviroment.isVirtualDevice();
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
    String appStorage =
        getSaucelabsAppId(AUTHORIZATION, getAppIdentifier(), getPlatform(), getAppVersion());

    MutableCapabilities caps =
        configureCommonCapabilities(
            "iOS", "storage:" + appStorage, getDeviceName(), getPlatformVersion(), "XCUITest");

    URL url;
    try {
      url = new URL(SAUCELABS_TESTS_URL);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
    return new IOSDriver(url, caps);
  }

  public static AndroidDriver configureSauceAndroid() {
    String appStorage =
        getSaucelabsAppId(AUTHORIZATION, getAppIdentifier(), getPlatform(), getAppVersion());

    MutableCapabilities caps =
        configureCommonCapabilities(
            "Android",
            "storage:" + appStorage,
            getDeviceName(),
            getPlatformVersion(),
            "UiAutomator2");
    URL url;
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
    switch (getBrowser()) {
      case "edge" -> {
        EdgeOptions edgeOptions = new EdgeOptions();
        edgeOptions.setPlatformName("Windows 11");
        edgeOptions.setBrowserVersion(setVersionBrowser());
        edgeOptions.setCapability("sauce:options", sauceOptions);
        return new RemoteWebDriver(url, edgeOptions);
      }
      case "firefox" -> {
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setPlatformName("Windows 11");
        firefoxOptions.setBrowserVersion(setVersionBrowser());
        firefoxOptions.setCapability("sauce:options", sauceOptions);
        return new RemoteWebDriver(url, firefoxOptions);
      }
      default -> {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setPlatformName("Windows 11");
        chromeOptions.setBrowserVersion(setVersionBrowser());
        chromeOptions.setCapability("sauce:options", sauceOptions);
        return new RemoteWebDriver(url, chromeOptions);
      }
    }
  }

  public static String setVersionBrowser() {
    String browser;
    checkValidVersion();
    SauceLabsService sauceLabsService = new SauceLabsService(new SauceLabsClient());
    if (getBrowser().equalsIgnoreCase("edge")) {
      browser = "MicrosoftEdge";
    } else {
      browser = LocalEnviroment.getBrowser();
    }
    Response<List<AppBrowserVersion>> response = sauceLabsService.getBrowserVersion(AUTHORIZATION);
    AppBrowserVersion[] browserVersionArray =
        response.getPayload().toArray(new AppBrowserVersion[0]);
    for (AppBrowserVersion Versions : browserVersionArray) {
      if (isMatchingVersion(Versions, browser, LocalEnviroment.getBrowserVersion())) {
        Logger.infoMessage(
            "The suggested version is Available: \n"
                + "Operating System: "
                + Versions.getOs()
                + "\nSuggested Version: "
                + Versions.getShort_version()
                + "\nBrowser Name: "
                + Versions.getApi_name());
        return LocalEnviroment.getBrowserVersion();
      } else if (LocalEnviroment.getBrowserVersion().equalsIgnoreCase("latest")
          && Objects.equals(Versions.getOs(), "Windows 11")
          && Objects.equals(Versions.getApi_name(), browser)) {
        Logger.infoMessage(
            "The suggested version is Available: \n"
                + "Operating System: "
                + Versions.getOs()
                + "\nSuggested Version: "
                + "latest"
                + "\nBrowser Name: "
                + Versions.getApi_name());
        return LocalEnviroment.getBrowserVersion();
      }
    }
    Logger.errorMessage(
        "The suggested Version is not available, check if the Enviroments Variables are correct.");
    throw new RuntimeException("The version you specified was not found or is invalid.");
  }

  public static boolean isMatchingVersion(
      AppBrowserVersion versionObj, String browser, String version) {
    return Objects.equals(versionObj.getApi_name(), browser)
        && Objects.equals(versionObj.getShort_version(), version)
        && Objects.equals(versionObj.getOs(), "Windows 11");
  }

  public static void checkValidVersion() {
    switch (LocalEnviroment.getBrowser()) {
      case "edge":
        {
          if (Integer.parseInt(LocalEnviroment.getBrowserVersion()) < 95) {
            throw new RuntimeException(
                "For Edge browsers, ensure that the version is greater than 95.");
          }
        }
      case "firefox":
        {
          {
            if (Integer.parseInt(LocalEnviroment.getBrowserVersion()) < 95) {
              throw new RuntimeException(
                  "For Firefox browsers, ensure that the version is greater than 95.");
            }
          }
        }
      case "chrome":
        {
          {
            if (Integer.parseInt(LocalEnviroment.getBrowserVersion()) < 90) {
              throw new RuntimeException(
                  "For Chrome browsers, ensure that the version is greater than 90.");
            }
          }
        }
    }
  }
}
