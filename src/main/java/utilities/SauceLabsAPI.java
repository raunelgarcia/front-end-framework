package utilities;


import java.io.IOException;
import java.lang.module.ModuleDescriptor.Version;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import org.json.JSONObject;
import saucelabs.api.Response;
import saucelabs.client.SauceLabsClient;
import saucelabs.dto.AppStorageResponse;
import saucelabs.service.SauceLabsService;

public class SauceLabsAPI {

  private static final String USER = LocalEnviroment.getUser();
  private static final String ACCESS_TOKEN = LocalEnviroment.getAccessToken();
  private static final String AUTHORIZATION = "Basic ".concat(Base64.getEncoder().encodeToString((USER + ":" + ACCESS_TOKEN).getBytes()));

  private static final SauceLabsService sauceLabsService = new SauceLabsService(new SauceLabsClient());

  public static JSONObject getAppStorageFilesByVersion(String version, Map<String, String> params)
      throws IOException, InterruptedException, IllegalArgumentException {
    String query = params.get("q");
    Response<AppStorageResponse> response = sauceLabsService.getAppStorageFiles(AUTHORIZATION, query);
    AppStorageResponse appStorageResponse = response.getPayload();
    System.out.println(response.getStatus());
    JSONObject files = new JSONObject(appStorageResponse);
    System.out.println(files);
    // Filter the JSON file. Go to "items" key, it has a list of json objects. Each json has a
    // "metadata" key with a "version" key.
    if (!Objects.equals(version, "latest")) {
      for (Object item : files.getJSONArray("items")) {
        JSONObject metadata = ((JSONObject) item).getJSONObject("metadata");
        if (metadata.getString("version").equals(version)) {
          return (JSONObject) item;
        }
      }
      // Raise an exception if the version is not found
      throw new IllegalArgumentException("Version not found: " + version);
    } else {
      // Get the latest version
      JSONObject latest = null;
      Version latestVersion = Version.parse("0");
      for (Object item : files.getJSONArray("items")) {
        JSONObject metadata = ((JSONObject) item).getJSONObject("metadata");
        Version currentVersion = Version.parse(metadata.getString("version"));
        if (currentVersion.compareTo(latestVersion) > 0) {
          latest = (JSONObject) item;
          latestVersion = currentVersion;
        }
      }
      return latest;
    }
  }

  public static String getAppFileId(String version, Map<String, String> params)
      throws IOException, InterruptedException, IllegalArgumentException {
    JSONObject jsonObject = getAppStorageFilesByVersion(version, params);
    return jsonObject.getString("id");
  }
}
