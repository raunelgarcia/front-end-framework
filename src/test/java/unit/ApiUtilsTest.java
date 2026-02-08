package unit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import saucelabs.api.ApiUtils;

class ApiUtilsTest {

  @Test
  void checkStatusCodeShouldPassWhenCodesMatch() {
    assertDoesNotThrow(() -> ApiUtils.checkStatusCode(200, 200));
  }

  @Test
  void checkStatusCodeShouldThrowAssertionErrorWhenCodesDoNotMatch() {
    assertThrows(AssertionError.class, () -> ApiUtils.checkStatusCode(404, 200));
  }
}
