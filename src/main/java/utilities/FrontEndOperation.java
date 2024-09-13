package utilities;

import exceptions.FrameworkTimeoutException;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.enums.Direction;

public class FrontEndOperation {
  private static <T> T doWithTryCatch(Supplier<T> action, T otherWise) {
    try {
      return action.get();
    } catch (RuntimeException var3) {
      return otherWise;
    }
  }

  public static boolean isNullOrEmpty(Object object) {
    if (Objects.isNull(object)) {
      return true;
    }
    if (object instanceof String) {
      return ((String) object).isEmpty();
    }
    if (object instanceof List) {
      return ((List<?>) object).isEmpty();
    }
    return false;
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
      waitSeconds(1);
    }
    return false;
  }

  public static void clickWhenVisible(WebElement element) {
    waitForVisibility(element);
    element.click();
  }

  public static void waitSeconds(long seconds) {
    try {
      TimeUnit.SECONDS.sleep(seconds);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public static void waitForVisibility(WebElement element) {
    waitFor(
        ExpectedConditions.visibilityOf(element), Constants.HIGH_TIMEOUT, ChronoUnit.SECONDS, true);
  }

  public static void waitForAnimationToFinish() {
    waitSeconds(Constants.LOW_TIMEOUT);
  }

  public static <K> void waitFor(
      ExpectedCondition<K> condition, long time, TemporalUnit unit, boolean shouldFail) {
    try {
      WebDriver driver = DriverConfiguration.getDriver();
      K result =
          new FluentWait<>(driver)
              .pollingEvery(Duration.ofMillis(1000))
              .withTimeout(Duration.of(time, unit))
              .withMessage(condition.toString())
              .ignoring(NoSuchElementException.class)
              .ignoring(StaleElementReferenceException.class)
              .until(
                  new ExpectedCondition<K>() {
                    final long start = System.currentTimeMillis();

                    @Override
                    public K apply(@Nullable WebDriver driver) {
                      long remainingTimeMs =
                          Duration.of(time, unit).toMillis() - (System.currentTimeMillis() - start);

                      System.out.printf(
                          "Remaining time for condition: %d ms. Condition is: %s%n",
                          remainingTimeMs, condition);
                      return condition.apply(driver);
                    }
                  });
    } catch (TimeoutException toe) {
      if (shouldFail) {
        throw new FrameworkTimeoutException(condition.toString(), time, unit);
      }
    }
  }

  public static void swipe(Direction direction) {
    swipe(direction, 0.3, 0.7);
  }

  public static void swipe(Direction direction, double minYRatio, double maxYRatio) {
    AppiumDriver driver = (AppiumDriver) DriverConfiguration.getDriver();
    Dimension window = driver.manage().window().getSize();
    int width = window.getWidth();
    int height = window.getHeight();
    swipe(direction, width, height, minYRatio, maxYRatio, false, driver);
  }

  public static void swipeElement(WebElement me, Direction direction) {
    AppiumDriver driver = (AppiumDriver) DriverConfiguration.getDriver();
    Dimension window = driver.manage().window().getSize();
    int width = window.getWidth();
    int height = window.getHeight();
    int meWidth = me.getLocation().getX();
    int meHeight = me.getLocation().getY();

    switch (direction) {
      case UP ->
          W3cActions.swipe(
              driver, new Point(meWidth / 2, meHeight), new Point(width / 2, height / 2), 500);
      case LEFT ->
          W3cActions.swipe(
              driver, new Point(width / 2, height / 4), new Point(meWidth, meHeight), 500);
      case RIGHT ->
          W3cActions.swipe(
              driver,
              new Point(me.getLocation().getX(), me.getLocation().getY()),
              new Point(width, me.getLocation().getY()),
              500);
      default -> {}
    }
  }

  private static void swipe(
      Direction direction,
      int width,
      int height,
      double minYRatio,
      double maxYRatio,
      boolean isByMobileElement,
      AppiumDriver driver) {
    int halfX = isByMobileElement ? width : (int) Math.floor(width / 2.0);
    int halfY = isByMobileElement ? height : (int) Math.floor(height / 2.0);
    int y = (int) Math.floor(height * minYRatio);
    int y2 = (int) Math.floor(height * maxYRatio);

    switch (direction) {
      case UP -> W3cActions.swipe(driver, new Point(halfX, y2), new Point(halfX, y), 500);
      case DOWN -> W3cActions.swipe(driver, new Point(halfX, y), new Point(halfX, y2), 500);
      case LEFT ->
          W3cActions.swipe(
              driver, new Point(Math.max(0, width - 10), halfY), new Point(10, halfY), 500);
      case RIGHT ->
          W3cActions.swipe(
              driver, new Point(10, halfY), new Point(Math.max(0, width - 10), halfY), 500);
      default -> {}
    }

    waitForAnimationToFinish();
  }

  public static void scrollToElement(WebElement element, Direction direction, boolean minScroll) {
    if (LocalEnviroment.isMobile()) {
      while (!isVisible(element)) {
        swipe(direction, 0.4, minScroll ? 0.5 : 0.6);
      }
    } else {
      W3cActions.getActions().moveToElement(element).perform();
    }
  }

  public static boolean compareTexts(WebElement element, String textCode) {
    return element.getText().equalsIgnoreCase(textCode);
  }

  public static <T> void checkThat(String validation, T actual, Matcher<T> expected) {
    StringBuilder message = new StringBuilder();
    message.append("Verifying that ").append(validation.toLowerCase()).append("<br>");
    message.append("(expectation: ").append(expected.toString()).append(")<br>");
    message.append("(actual: ").append(actual.toString()).append(")<br>");

    try {
      MatcherAssert.assertThat(message.toString(), actual, expected);
      AllureReport.addComparation(message.toString(), true);
    } catch (AssertionError error) {
      AllureReport.addComparation(message.toString(), false);
      throw error;
    }
  }

  public static void switchToNativeContext() {
    AndroidDriver driver = (AndroidDriver) DriverConfiguration.getDriver();
    if (LocalEnviroment.isMobile()) driver.context("NATIVE_APP");
  }

  public static void switchToWebViewContext() {
    AndroidDriver driver = (AndroidDriver) DriverConfiguration.getDriver();
    if (LocalEnviroment.isMobile()) {
      String webViewContext = null;
      for (String context : driver.getContextHandles()) {
        if (context.toLowerCase().contains("webview")) {
          webViewContext = context;
          break;
        }
      }
      if (webViewContext != null) {
        driver.context(webViewContext);
      } else {
        Logger.errorMessage("Not WebView context found.");
      }
    }
  }

  public static void switchToTab(int index, boolean close) {
    WebDriver driver = DriverConfiguration.getDriver();
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    wait.until(ExpectedConditions.numberOfWindowsToBe(index + 1));

    List<String> windowHandles = new ArrayList<>(driver.getWindowHandles());
    if (index < 0 || index >= windowHandles.size()) {
      throw new IllegalArgumentException("Invalid index " + index);
    }

    String windowToSwitch = windowHandles.get(index);
    driver.switchTo().window(windowToSwitch);

    if (close) driver.close();
  }
}
