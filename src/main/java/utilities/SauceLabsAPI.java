package utilities;

import static utilities.Constants.SAUCELABS_API_URL;

import java.io.IOException;
import java.lang.module.ModuleDescriptor.Version;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import org.json.JSONObject;

public class SauceLabsAPI {

  private static final String USER = LocalEnviroment.getUser();
  private static final String ACCESS_TOKEN = LocalEnviroment.getAccessToken();
  private static final String AUTHORIZATION =
      Base64.getEncoder().encodeToString((USER + ":" + ACCESS_TOKEN).getBytes());

  private static final HttpClient client = HttpClient.newHttpClient();

  private static HttpRequest createGETRequest(String url) {
    return HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Authorization", "Basic " + AUTHORIZATION)
        .GET()
        .build();
  }

  private static String getQueryParams(Map<String, String> params) {
    StringJoiner sj = new StringJoiner("&");
    for (Map.Entry<String, String> entry : params.entrySet()) {
      sj.add(entry.getKey() + "=" + entry.getValue());
    }
    return sj.toString();
  }

  public static JSONObject getAppStorageFiles(Map<String, String> params)
      throws IOException, InterruptedException {
    String url = SAUCELABS_API_URL + "v1/storage/files";
    if (params != null && !params.isEmpty()) {
      url += "?" + getQueryParams(params);
    }
    HttpRequest request = createGETRequest(url);
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    return new JSONObject(response.body());
  }

  public static JSONObject getAppStorageFilesByVersion(String version, Map<String, String> params)
      throws IOException, InterruptedException, IllegalArgumentException {
    JSONObject files = getAppStorageFiles(params);
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
