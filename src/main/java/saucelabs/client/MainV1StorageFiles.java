package saucelabs.client;

import saucelabs.api.Response;
import saucelabs.dto.AppStorageResponse;
import saucelabs.service.SauceLabsService;
import utilities.Constants;
import utilities.Logger;
import utilities.SaucelabsDriverConfiguration;

public class MainV1StorageFiles {

  private static SauceLabsClient client = new SauceLabsClient();
  private static SauceLabsService service = new SauceLabsService(client);

  public static void main(String[] args) {

    String query = "1";
    String kind = "human";
    Integer perPage = 1;

    Response<AppStorageResponse> response =
        service.getV1StorageFiles(Constants.AUTHORIZATION, query, kind, perPage);

    Logger.infoMessage("Items: " + response.getPayload().getItems());

    Logger.infoMessage(
        SaucelabsDriverConfiguration.getSaucelabsAppId(
            Constants.AUTHORIZATION, query, kind, Integer.toString(perPage)));
  }
}
