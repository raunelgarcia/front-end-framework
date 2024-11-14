package saucelabs.api;

import static org.hamcrest.Matchers.is;

import org.hamcrest.MatcherAssert;
import utilities.Logger;

public class ApiUtils {

  /**
   * Check if the status code of the response is the expected one.
   *
   * @param actualStatusCode The actual status code of the response
   * @param expectedStatusCode The expected status code
   */
  public static void checkStatusCode(int actualStatusCode, int expectedStatusCode) {
    StringBuilder message = new StringBuilder();
    message.append("Verifying that ").append("the status code of the response is OK ".toLowerCase());
    message.append("(expectation: ").append(is(expectedStatusCode)).append(") ");
    message.append("(actual: ").append(actualStatusCode).append(")");

    try {
      MatcherAssert.assertThat(message.toString(), actualStatusCode, is(expectedStatusCode));
      Logger.infoMessage(message.toString());
    } catch (AssertionError error) {
      Logger.errorMessage(message.toString());
      throw error;
    }
  }
}
