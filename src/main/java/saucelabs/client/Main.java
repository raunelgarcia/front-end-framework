package saucelabs.client;

import java.util.Base64;
import saucelabs.service.SauceLabsService;

public class Main {

  private static SauceLabsClient client = new SauceLabsClient();
  private static SauceLabsService service = new SauceLabsService(client);

  // Credenciales para autenticación básica
  private static final String USERNAME = "oauth-diego.gonzalezsanz-3f445";
  private static final String ACCESS_KEY = "03e5147c-c031-4721-9551-f8cb7ae3dbaf";

  public static void main(String[] args) {

    String authHeader = "Basic " + encodeCredentials(USERNAME, ACCESS_KEY);
    boolean response = service.getVerifyDeviceExists(authHeader);

    System.out.println("¿Existe el dispositivo? " + response);
  }

  private static String encodeCredentials(String username, String accessKey) {
    String auth = username + ":" + accessKey;
    return Base64.getEncoder().encodeToString(auth.getBytes());
  }
}
