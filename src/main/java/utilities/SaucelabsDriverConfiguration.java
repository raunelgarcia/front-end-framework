package utilities;

import static org.apache.http.HttpStatus.SC_OK;
import static utilities.Constants.AUTHORIZATION;
import static utilities.LocalEnviroment.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.openqa.selenium.MutableCapabilities;
import saucelabs.api.ApiUtils;
import saucelabs.api.Response;
import saucelabs.client.SauceLabsClient;
import saucelabs.dto.AppBrowserVersion;
import saucelabs.dto.AppStorageItemMetadataResponse;
import saucelabs.dto.AppStorageResponse;
import saucelabs.dto.AppStorageUserResponse;
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
   * Verifies if a device exists in Sauce Labs
   *
   * <p>This method fetches the app's devices from Sauce Labs, filters the results to check if a
   * device with the specified name exists the name of device, and returns whether it exists.
   *
   * @param authorization The authorization token required to access the Sauce Labs API.
   * @return Boolean indicating wether the device exists
   */
  public static boolean getVerifyDeviceExist(String authorization) {

    SauceLabsService sauceLabsService = new SauceLabsService(new SauceLabsClient());
    Response<List<AppStorageUserResponse>> response = sauceLabsService.getAllDevices(authorization);
    ApiUtils.checkStatusCode(response.getStatus(), SC_OK);

    for (AppStorageUserResponse device : response.getPayload()) {
      if (device.getName().equals(LocalEnviroment.getDeviceName())) {
        return true;
      }
    }

    return false;
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

    boolean iosCheck = isAndroid() || metadata.getIs_simulator() == isVirtualDevice();
    return versionMatches && iosCheck;
  }

  public static MutableCapabilities getSauceOptions() {
    MutableCapabilities sauceOptions = new MutableCapabilities();
    sauceOptions.setCapability("appiumVersion", getAppiumVersion());
    sauceOptions.setCapability("username", getUser());
    sauceOptions.setCapability("accessKey", getAccessToken());
    sauceOptions.setCapability("build", "selenium-build-VNFHT");
    sauceOptions.setCapability("name", getPlatform() + "_Test");

    return sauceOptions;
  }

  public static void setSauceWebCapabilities(MutableCapabilities capabilities) {
    if (!getBrowser().equalsIgnoreCase("safari")) {
      capabilities.setCapability("platformName", "Windows 11");
    } else {
      capabilities.setCapability("platformName", "macOS 13");
    }

    capabilities.setCapability("browserVersion", setVersionBrowser());
    MutableCapabilities sauceOptions = getSauceOptions();
    sauceOptions.setCapability("extendedDebugging", true);
    capabilities.setCapability("sauce:options", sauceOptions);
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
          if (!LocalEnviroment.getBrowserVersion().equalsIgnoreCase("latest")
              && Integer.parseInt(LocalEnviroment.getBrowserVersion()) < 95) {
            throw new RuntimeException(
                "For Edge browsers, ensure that the version is greater than 95.");
          }
        }
      case "firefox":
        {
          {
            if (!LocalEnviroment.getBrowserVersion().equalsIgnoreCase("latest")
                && Integer.parseInt(LocalEnviroment.getBrowserVersion()) < 95) {
              throw new RuntimeException(
                  "For Firefox browsers, ensure that the version is greater than 95.");
            }
          }
        }
      case "chrome":
        {
          {
            if (!LocalEnviroment.getBrowserVersion().equalsIgnoreCase("latest")
                && Integer.parseInt(LocalEnviroment.getBrowserVersion()) < 90) {
              throw new RuntimeException(
                  "For Chrome browsers, ensure that the version is greater than 90.");
            }
          }
        }
    }
  }
}
