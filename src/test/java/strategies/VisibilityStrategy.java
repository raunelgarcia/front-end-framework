package strategies;

import java.util.function.Supplier;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utilities.Constants.Time;

public class VisibilityStrategy {

  private static <T> T doWithTryCatch(Supplier<T> action, T otherWise) {
    try {
      return action.get();
    } catch (RuntimeException var3) {
      return otherWise;
    }
  }

  public static <T extends WebElement> boolean isVisible(final T e) {
    return doWithTryCatch(e::isDisplayed, false);
  }

  public static <T extends WebElement> boolean isVisible(final T e, long timeoutSeconds) {
    long startTime = System.currentTimeMillis();
    long endTime = startTime + timeoutSeconds * 1000;

    while (System.currentTimeMillis() < endTime) {
      if (doWithTryCatch(e::isDisplayed, false)) {
        return true;
      }
      WaitStrategy.waitSeconds(1);
    }
    return false;
  }

  public static void clickWhenVisible(WebElement element, WebDriver driver) {
    WaitStrategy.waitForVisibility(element, driver);
    element.click();
  }
}
