package main_file;

import saucelabs.api.Response;
import saucelabs.client.SauceLabsClient;
import saucelabs.service.SauceLabsService;
import utilities.Constants;
import java.util.List;

import saucelabs.dto.AppBrowserResponse;
import saucelabs.dto.AppBrowserVersion;

public class Main {

  private static SauceLabsClient client = new SauceLabsClient();
  private static SauceLabsService service = new SauceLabsService(client);

  public static void main(String[] args) {

    Response<AppBrowserResponse> response = service.getBrowserVersion(Constants.AUTHORIZATION);

    // Verificar que la respuesta no sea nula y tenga un código de estado OK
    if (response != null && response.getStatus() == 200) {
        // Obtener el payload de la respuesta
        AppBrowserResponse appBrowserResponse = response.getPayload();

        if (appBrowserResponse != null) {
            // Obtener la lista de versiones de navegador
            List<AppBrowserVersion> browserVersions = appBrowserResponse.getItems();

            // Verificar si la lista no es nula y no está vacía
            if (browserVersions != null && !browserVersions.isEmpty()) {
                // Recorrer la lista y procesar cada elemento
                for (AppBrowserVersion version : browserVersions) {
                    System.out.println("Short Version: " + version.getShort_version());
                    System.out.println("Long Name: " + version.getLong_name());
                    System.out.println("API Name: " + version.getApi_name());
                    System.out.println("Long Version: " + version.getLong_version());
                    System.out.println("Automation Backend: " + version.getOs());
                    System.out.println("-----------");
                }
            } else {
                System.out.println("No browser versions found.");
            }
        } else {
            System.out.println("AppBrowserResponse payload is null.");
        }
    } else {
        System.out.println("Failed to get a valid response from the service.");
    }

  }
}
