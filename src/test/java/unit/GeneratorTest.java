package unit;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.junit.jupiter.api.Test;
import utilities.Generator;

class GeneratorTest {

  @Test
  void generateRandomStringShouldRespectFixedLength() {
    String value = Generator.generateRandomString(12);
    assertEquals(12, value.length());
  }

  @Test
  void generateRandomStringWithCharsetShouldUseOnlyProvidedCharacters() {
    String charset = "abc";
    String value = Generator.generateRandomString(30, charset);

    assertEquals(30, value.length());
    assertTrue(value.chars().allMatch(ch -> charset.indexOf(ch) >= 0));
  }

  @Test
  void generateRandomStringWithRangeShouldRespectBounds() {
    String value = Generator.generateRandomString(5, 10);
    assertTrue(value.length() >= 5 && value.length() <= 10);
  }

  @Test
  void generateRandomIntShouldStayWithinRange() {
    for (int i = 0; i < 200; i++) {
      int value = Generator.generateRandomInt(3, 7);
      assertTrue(value >= 3 && value <= 7);
    }
  }

  @Test
  void generateRandomEmailShouldBeLowercaseAndUseGmailDomain() {
    String email = Generator.generateRandomEmail(6, 10);
    assertTrue(email.endsWith("@gmail.com"));
    assertEquals(email, email.toLowerCase());
  }

  @Test
  void getCurrentDateShouldReturnIsoDate() {
    String date = Generator.getCurrentDate();
    assertDoesNotThrow(() -> LocalDate.parse(date));
  }

  @Test
  void getCurrentDateShouldRespectPattern() {
    String date = Generator.getCurrentDate("dd/MM/yyyy");
    assertEquals(10, date.length());
    assertThrows(DateTimeParseException.class, () -> LocalDate.parse(date));
  }

  @Test
  void getRandomElementShouldReturnElementFromList() {
    List<String> data = List.of("one", "two", "three");
    String item = Generator.getRandomElement(data);
    assertTrue(data.contains(item));
  }

  @Test
  void getRandomElementShouldFailForNullOrEmptyList() {
    assertThrows(IllegalArgumentException.class, () -> Generator.getRandomElement(null));
    assertThrows(IllegalArgumentException.class, () -> Generator.getRandomElement(List.of()));
  }
}
