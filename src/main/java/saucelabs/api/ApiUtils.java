package saucelabs.api;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import utilities.FrontEndOperation;

public class ApiUtils {
  public static void checkStatusCode(int actualStatusCode, int expectedStatusCode) {
    Matcher<Integer> expectedStatusCodeMatcher = Matchers.equalTo(expectedStatusCode);
    FrontEndOperation.checkThat(
        "the status code is valid", actualStatusCode, expectedStatusCodeMatcher);
  }
}
