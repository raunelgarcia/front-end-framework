package tests;

import static org.hamcrest.Matchers.*;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

public class DifferentTests {

  @Test
  public void testIntegers() {
    int result = 5 + 3;
    checkThat("integer addition", result, is(equalTo(8)));
  }

  @Test
  public void testFloats() {
    double result = 5.2 + 3.3;
    checkThat("float addition", result, closeTo(8.5, 0.01));
  }

  @Test
  public void testStrings() {
    String str = "Hello, world!";
    checkThat("the string", str, containsString("world"));
  }

  @Test
  public void testObjects() {
    MyObject obj1 = new MyObject(10);
    MyObject obj2 = new MyObject(10);
    checkThat("the objects", obj1, is(sameInstance(obj2)));
  }

  private <T> void checkThat(String validation, T actual, Matcher<T> expected) {
    StringBuilder message = new StringBuilder("Verifying that ").append(validation.toLowerCase());
    message.append(" (expectation: ").append(expected.toString()).append(")");
    try {
      MatcherAssert.assertThat(message.toString(), actual, expected);
      System.out.println("Test passed: " + message);
    } catch (AssertionError error) {
      System.out.println("Test failed: " + message);
      throw error;
    }
  }
}

class MyObject {
  private final int value;

  public MyObject(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
