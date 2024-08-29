package saucelabs.service;

import jakarta.ws.rs.core.GenericType;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import saucelabs.api.Response;
import saucelabs.api.ApiUtils;
import saucelabs.client.SauceLabsClient;
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
   * @return A Response object wrapping the AppStorageItemsResponse
   */
  public Response<AppStorageResponse> getAppStorageFiles(String authorization, String query) {
    // Perform the API call using SauceLabsClient and wrap the result in a Response object
    Response<AppStorageResponse> response = sauceLabsClient.call(
        () -> sauceLabsClient.getAPI().getAppStorageFiles(authorization, query),
        Optional.empty(), // No direct class provided, we'll use GenericType
        new GenericType<AppStorageResponse>() {} // Use GenericType for complex types
        );
    return response;
  }
}
