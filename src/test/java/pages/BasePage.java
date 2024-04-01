package pages;

import static java.lang.Math.floor;
import static java.lang.Math.max;
import static java.lang.System.currentTimeMillis;
import static utilities.Constants.HIGH_TIMEOUT;
import static utilities.Constants.LOW_TIMEOUT;

import exceptions.FrameworkTimeoutException;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import utilities.LocalEnviroment;
import utilities.Text;
import utilities.W3cActions;
import utilities.enums.Direction;

public class BasePage {
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
      waitSeconds(1);
    }
    return false;
  }

  public static void clickWhenVisible(WebElement element, WebDriver driver) {
    waitForVisibility(element, driver);
    element.click();
  }

  public static void waitSeconds(long seconds) {
    try {
      TimeUnit.SECONDS.sleep(seconds);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public static void waitForVisibility(WebElement element, WebDriver driver) {
    waitFor(
        ExpectedConditions.visibilityOf(element),
        driver,
        HIGH_TIMEOUT,
        ChronoUnit.SECONDS,
        true);
  }

  public static void waitForAnimationToFinish() {
    waitSeconds(LOW_TIMEOUT);
  }

  public static <K> void waitFor(
      ExpectedCondition<K> condition,
      WebDriver driver,
      long time,
      TemporalUnit unit,
      boolean shouldFail) {
    try {
      K result =
          new FluentWait<>(driver)
              .pollingEvery(Duration.ofMillis(1000))
              .withTimeout(Duration.of(time, unit))
              .withMessage(condition.toString())
              .ignoring(NoSuchElementException.class)
              .ignoring(StaleElementReferenceException.class)
              .until(
                  new ExpectedCondition<K>() {
                    final long start = currentTimeMillis();

                    @Override
                    public K apply(@Nullable WebDriver driver) {
                      long remainingTimeMs =
                          Duration.of(time, unit).toMillis() - (currentTimeMillis() - start);

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

  public static void swipe(Direction direction, AppiumDriver driver) {
    swipe(direction, 0.3, 0.7, driver);
  }

  public static void swipe(
      Direction direction, double minYRatio, double maxYRatio, AppiumDriver driver) {
    Dimension window = driver.manage().window().getSize();
    int width = window.getWidth();
    int height = window.getHeight();
    swipe(direction, width, height, minYRatio, maxYRatio, false, driver);
  }

  public static void swipeElement(WebElement me, Direction direction, AppiumDriver driver) {
    Dimension window = driver.manage().window().getSize();
    int width = window.getWidth();
    int height = window.getHeight();
    int meWidth = me.getLocation().getX();
    int meHeight = me.getLocation().getY();

    switch (direction) {
      case UP:
        W3cActions.swipe(
            driver, new Point(meWidth / 2, meHeight), new Point(width / 2, height / 2), 500);
        break;
      case LEFT:
        W3cActions.swipe(
            driver, new Point(width / 2, height / 4), new Point(meWidth, meHeight), 500);
        break;
      case RIGHT:
        W3cActions.swipe(
            driver,
            new Point(me.getLocation().getX(), me.getLocation().getY()),
            new Point(width, me.getLocation().getY()),
            500);
        break;
      default:
        break;
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
    int halfX = isByMobileElement ? width : (int) floor(width / 2.0);
    int halfY = isByMobileElement ? height : (int) floor(height / 2.0);
    int y = (int) floor(height * minYRatio);
    int y2 = (int) floor(height * maxYRatio);

    switch (direction) {
      case UP:
        W3cActions.swipe(driver, new Point(halfX, y2), new Point(halfX, y), 500);
        break;
      case DOWN:
        W3cActions.swipe(driver, new Point(halfX, y), new Point(halfX, y2), 500);
        break;
      case LEFT:
        W3cActions.swipe(driver, new Point(max(0, width - 10), halfY), new Point(10, halfY), 500);
        break;
      case RIGHT:
        W3cActions.swipe(driver, new Point(10, halfY), new Point(max(0, width - 10), halfY), 500);
        break;
      default:
        break;
    }

    waitForAnimationToFinish();
  }

  public static void scrollToElement(
      WebElement element, Direction direction, boolean minScroll, WebDriver driver) {
    while (!isVisible(element)) {
      if (LocalEnviroment.isMobile()) {
        swipe(direction, 0.4, minScroll ? 0.5 : 0.6, (AppiumDriver) driver);
      } else {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,200)");
      }
    }
  }

  public static boolean compareTexts(WebElement element, String textCode) {
    return element.getText().equalsIgnoreCase(Text.get(textCode));
  }

  public static void switchToNativeContext(AndroidDriver driver) {
    if (LocalEnviroment.isMobile()) driver.context("NATIVE_APP");
  }

  public static void switchToWebViewContext(AndroidDriver driver) {
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
        System.out.println("Not WebView context found.");
      }
    }
  }
}
