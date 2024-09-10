package saucelabs.api;

import static org.hamcrest.Matchers.is;

import utilities.FrontEndOperation;

public class ApiUtils {

  /**
   * Check if the status code of the response is the expected one.
   *
   * @param actualStatusCode The actual status code of the response
   * @param expectedStatusCode The expected status code
   */
  public static void checkStatusCode(int actualStatusCode, int expectedStatusCode) {
    FrontEndOperation.checkThat(
        "the status code of the response is OK", actualStatusCode, is(expectedStatusCode));
  }
}
