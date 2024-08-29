package saucelabs.api;

import java.lang.module.ModuleDescriptor.Version;
import java.util.Base64;
import java.util.Comparator;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import saucelabs.client.SauceLabsClient;
import saucelabs.dto.AppStorageItemMetadataResponse;
import saucelabs.dto.AppStorageItemsResponse;
import saucelabs.dto.AppStorageResponse;
import saucelabs.service.SauceLabsService;
import utilities.FrontEndOperation;
import utilities.LocalEnviroment;

import static org.apache.http.HttpStatus.SC_OK;

public class ApiUtils {
  public static void checkStatusCode(int actualStatusCode, int expectedStatusCode) {
    Matcher<Integer> expectedStatusCodeMatcher = Matchers.equalTo(expectedStatusCode);
    FrontEndOperation.checkThat(
        "the status code of the response is OK", actualStatusCode, expectedStatusCodeMatcher);
  }

  private static Boolean checkIosApp(AppStorageItemMetadataResponse metadata) {
    Boolean isSimulator = metadata.getIs_simulator();
    if (isSimulator == null) {
      return false;
    }
    if (LocalEnviroment.getDeviceName().equals("iPhone Simulator")) {
      return isSimulator;
    } else {
      return !isSimulator;
    }
  }

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

  public static AppStorageItemsResponse getAppStorageFilesByVersion(
      String authorization, String appId, String version) {
    // Check that appId is not null
    if (appId.isBlank()) {
      throw new IllegalArgumentException("AppIdentifier is not set");
    }

    // Retrieve app storage response
    SauceLabsService sauceLabsService = new SauceLabsService(new SauceLabsClient());
    Response<AppStorageResponse> response =
        sauceLabsService.getAppStorageFiles(authorization, appId);
    ApiUtils.checkStatusCode(response.getStatus(), SC_OK);
    AppStorageResponse appStorageResponse = response.getPayload();

    // Handle specific version request
    if (!"latest".equals(version)) {
      return appStorageResponse.getItems().stream()
          .filter(item -> isMatchingVersion(item.getMetadata(), version))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Version not found: ".concat(version)));
    }

    // Handle latest version request
    return appStorageResponse.getItems().stream()
        .filter(item -> isPlatformCompatible(item.getMetadata()))
        .max(Comparator.comparing(item -> Version.parse(getVersion(item.getMetadata()))))
        .orElseThrow(() -> new IllegalArgumentException("No suitable latest version found"));
  }

  private static boolean isMatchingVersion(
      AppStorageItemMetadataResponse metadata, String version) {
    return getVersion(metadata).equals(version) && isPlatformCompatible(metadata);
  }

  private static boolean isPlatformCompatible(AppStorageItemMetadataResponse metadata) {
    return LocalEnviroment.isAndroid() || checkIosApp(metadata);
  }

  public static String getAppFileId(String authorization, String appId, String version) {
    AppStorageItemsResponse appStorageItemsResponse =
        getAppStorageFilesByVersion(authorization, appId, version);
    return appStorageItemsResponse.getId();
  }

  public static void main(String[] args) {
    String authorization =
        "Basic "
            + Base64.getEncoder()
                .encodeToString(
                    (LocalEnviroment.getUser() + ":" + LocalEnviroment.getAccessToken())
                        .getBytes());
    String appId = LocalEnviroment.getAppIdentifier();
    String version = LocalEnviroment.getAppVersion();
    String fileId = getAppFileId(authorization, appId, version);
    System.out.println(fileId);
    // Assert the file id matches the environment variable AppId
    String realFileId = System.getenv("AppId");
    if (!fileId.equals(realFileId)) {
      if (realFileId == null) {
        throw new IllegalArgumentException("Environment variable AppId is not set");
      }
      throw new IllegalArgumentException("File id does not match the environment variable AppId");
    }
  }
}
