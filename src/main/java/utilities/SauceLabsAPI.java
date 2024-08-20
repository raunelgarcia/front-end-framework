package utilities;

import static utilities.Constants.SAUCELABS_API_URL;

import java.io.File;
import java.io.IOException;
import java.lang.module.ModuleDescriptor.Version;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import org.json.JSONArray;
import org.json.JSONObject;

public class SauceLabsAPI {

  private static final String USER = LocalEnviroment.getUser();
  private static final String ACCESS_TOKEN = LocalEnviroment.getAccessToken();
  private static final String AUTHORIZATION = Base64.getEncoder().encodeToString((USER + ":" + ACCESS_TOKEN).getBytes());

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

  private static void writeToFile(byte[] content, String path) throws IOException {
    File file = new File(path);
    file.createNewFile();
    Files.write(file.toPath(), content);
  }

  public static JSONObject getAppStorageFiles(Map<String, String> params) throws IOException, InterruptedException {
    String url = SAUCELABS_API_URL + "v1/storage/files";
    if (params != null && !params.isEmpty()) {
      url += "?" + getQueryParams(params);
    }
    HttpRequest request = createGETRequest(url);
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    return new JSONObject(response.body());
  }

  public static JSONObject uploadApp(String appPath) throws IOException, InterruptedException {
    String url = SAUCELABS_API_URL + "v1/storage/upload";
    Path filePath = Path.of(appPath);
    String fileName = filePath.getFileName().toString();

    // Boundary for multipart form-data
    String boundary = "===" + System.currentTimeMillis() + "===";
    String contentType = "multipart/form-data; boundary=" + boundary;

    // Create the multipart body
    String filePartHeader = "--" + boundary + "\r\n" +
            "Content-Disposition: form-data; name=\"payload\"; filename=\"" + fileName + "\"\r\n" +
            "Content-Type: application/octet-stream\r\n\r\n";
    String endPart = "\r\n--" + boundary + "--\r\n";

    byte[] fileBytes = Files.readAllBytes(filePath);
    byte[] bodyStart = filePartHeader.getBytes();
    byte[] bodyEnd = endPart.getBytes();

    // Combine all parts to form the request body
    byte[] requestBody = new byte[bodyStart.length + fileBytes.length + bodyEnd.length];
    System.arraycopy(bodyStart, 0, requestBody, 0, bodyStart.length);
    System.arraycopy(fileBytes, 0, requestBody, bodyStart.length, fileBytes.length);
    System.arraycopy(bodyEnd, 0, requestBody, bodyStart.length + fileBytes.length, bodyEnd.length);

    // Build the HTTP request
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Basic " + AUTHORIZATION)
            .header("Content-Type", contentType)
            .POST(HttpRequest.BodyPublishers.ofByteArray(requestBody))
            .build();

    // Send the request
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    // Parse the response
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
    }
    else {
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

  public static JSONArray getJobs(Map<String, String> params, Boolean rdc) throws IOException, InterruptedException {
    String url = SAUCELABS_API_URL;
    if (rdc) {
      url += "/v1/rdc/jobs";
    }
    else {
      url += "/rest/v1/" + USER + "/jobs";
    }
    if (params != null && !params.isEmpty()) {
      url += "?" + getQueryParams(params);
    }
    HttpRequest request = createGETRequest(url);
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    return new JSONArray(response.body());
  }

  public static JSONObject getJob(String jobId, Boolean rdc) throws IOException, InterruptedException {
    String url = SAUCELABS_API_URL;
    if (rdc) {
      url += "/v1/rdc/jobs/" + jobId;
    }
    else {
      url += "/rest/v1/" + USER + "/jobs/" + jobId;
    }
    HttpRequest request = createGETRequest(url);
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    return new JSONObject(response.body());
  }

  public static JSONObject getJobAssets(String jobId) throws IOException, InterruptedException {
    String url = SAUCELABS_API_URL + "/rest/v1/" + USER + "/jobs/" + jobId + "/assets";
    HttpRequest request = createGETRequest(url);
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    return new JSONObject(response.body());
  }

  public static void getJobAsset(String jobId, String assetId, String path, Boolean rdc) throws IOException, InterruptedException {
    String url;
    if (rdc) {
      JSONObject job = getJob(jobId, true);
      url = job.getString(assetId);
    }
    else {
      url = SAUCELABS_API_URL + "/rest/v1/" + USER + "/jobs/" + jobId + "/assets/" + assetId;
    }
    HttpRequest request = createGETRequest(url);
    HttpResponse<byte[]> response = client.send(request, BodyHandlers.ofByteArray());
    writeToFile(response.body(), path);
  }
}
