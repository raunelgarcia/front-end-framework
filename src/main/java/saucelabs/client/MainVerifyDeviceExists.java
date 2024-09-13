package saucelabs.client;

import java.util.List;
import saucelabs.api.Response;
import saucelabs.dto.AppStorageUserResponse;
import saucelabs.service.SauceLabsService;
import utilities.Constants;
import utilities.Logger;
import utilities.SaucelabsDriverConfiguration;

public class MainVerifyDeviceExists {

  private static final SauceLabsClient client = new SauceLabsClient();
  private static final SauceLabsService service = new SauceLabsService(client);

  public static void main(String[] args) {

    Response<List<AppStorageUserResponse>> response =
        service.getAllDevices(Constants.AUTHORIZATION);

    Logger.infoMessage("¿Existe el dispositivo? " + response.getPayload());

    Logger.infoMessage(
        SaucelabsDriverConfiguration.getVerifyDeviceExist(Constants.AUTHORIZATION)
            ? "Device exist"
            : "Device not exist");
  }
}
