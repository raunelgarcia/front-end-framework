package strategies;

import java.util.function.Supplier;
import org.openqa.selenium.WebElement;

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
}
