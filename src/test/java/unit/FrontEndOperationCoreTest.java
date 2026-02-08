package unit;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import utilities.FrontEndOperation;

class FrontEndOperationCoreTest {

  @Test
  void isNullOrEmptyShouldHandleNullStringsAndLists() {
    assertTrue(FrontEndOperation.isNullOrEmpty(null));
    assertTrue(FrontEndOperation.isNullOrEmpty(""));
    assertTrue(FrontEndOperation.isNullOrEmpty(List.of()));
  }

  @Test
  void isNullOrEmptyShouldReturnFalseForValues() {
    assertFalse(FrontEndOperation.isNullOrEmpty("value"));
    assertFalse(FrontEndOperation.isNullOrEmpty(List.of("item")));
    assertFalse(FrontEndOperation.isNullOrEmpty(123));
  }
}
