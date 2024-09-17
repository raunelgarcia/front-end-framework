package saucelabs.service;

import jakarta.ws.rs.core.GenericType;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import saucelabs.api.Response;
import saucelabs.client.SauceLabsClient;
import saucelabs.dto.AppBrowserVersion;
import saucelabs.dto.AppStorageResponse;

@Service
public class SauceLabsService {

  private final SauceLabsClient sauceLabsClient;

  @Autowired
  public SauceLabsService(SauceLabsClient sauceLabsClient) {
    this.sauceLabsClient = sauceLabsClient;
  }

  /**
   * Retrieves app storage files from Sauce Labs using the given authorization and query.
   *
   * @param authorization The authorization token
   * @param query The query string
   * @param kind The kind of platform (android or ios)
   * @param perPage The number of items per page
   * @return A Response object wrapping the AppStorageItemsResponse
   */
  public Response<AppStorageResponse> getV1StorageFiles(
      String authorization, String query, String kind, Integer perPage) {
    // Perform the API call using SauceLabsClient and wrap the result in a Response object
    return sauceLabsClient.call(
        () -> sauceLabsClient.getAPI().getV1StorageFiles(authorization, query, kind, perPage),
        Optional.empty(), // No direct class provided, we'll use GenericType
        new GenericType<AppStorageResponse>() {} // Use GenericType for complex types
        );
  }

  public Response<List<AppBrowserVersion>> getBrowserVersion(String authorization) {
    return sauceLabsClient.call(
        () -> sauceLabsClient.getAPI().getV1InfoPlatformsAutomationApi(authorization, "webdriver"),
        Optional.empty(),
        new GenericType<List<AppBrowserVersion>>() {});
  }
}
