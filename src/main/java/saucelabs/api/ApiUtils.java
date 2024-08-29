package saucelabs.api;

import java.lang.module.ModuleDescriptor.Version;
import java.util.Base64;

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
    if (LocalEnviroment.isAndroid()) {
        return metadata.getVersion();
        } else {
        return metadata.getShort_version();
    }
}
  public static AppStorageItemsResponse getAppStorageFilesByVersion(String authorization, String appId, String version) {
    SauceLabsService sauceLabsService = new SauceLabsService(new SauceLabsClient());
    Response<AppStorageResponse> response =
        sauceLabsService.getAppStorageFiles(authorization, appId);
    ApiUtils.checkStatusCode(response.getStatus(), SC_OK);
    AppStorageResponse appStorageResponse = response.getPayload();

    if (!version.equals("latest")) {
      for (AppStorageItemsResponse item : appStorageResponse.getItems()) {
        AppStorageItemMetadataResponse metadata = item.getMetadata();
          if (getVersion(metadata).equals(version) && (LocalEnviroment.isAndroid() || checkIosApp(metadata))) {
            return item;
          }
        }
      throw new IllegalArgumentException("Version not found: " + version);
    } else {
      Version latestVersion = Version.parse("0");
      AppStorageItemsResponse latest = null;
      for (AppStorageItemsResponse item : appStorageResponse.getItems()) {
        AppStorageItemMetadataResponse metadata = item.getMetadata();
        Version currentVersion = Version.parse(getVersion(metadata));
        if (currentVersion.compareTo(latestVersion) > 0 && (LocalEnviroment.isAndroid() || checkIosApp(metadata))) {
          System.out.println(metadata);
          latestVersion = currentVersion;
          latest = item;
        }
      }
      return latest;
    }
  }

 public static String getAppFileId(String authorization, String appId, String version) {
    AppStorageItemsResponse appStorageItemsResponse = getAppStorageFilesByVersion(authorization, appId, version);
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
      throw new IllegalArgumentException("File id does not match the environment variable AppId");
    }
  }
}
