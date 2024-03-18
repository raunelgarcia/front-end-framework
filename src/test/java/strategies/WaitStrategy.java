package strategies;

import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import exceptions.FrameworkTimeoutException;
import utilities.Constants;
import utilities.Constants.Time;

public class WaitStrategy {

  public static void waitSeconds(long seconds) {
    try {
      TimeUnit.SECONDS.sleep(seconds);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public static void waitForVisibility(WebElement element, WebDriver driver) {
    waitFor(ExpectedConditions.visibilityOf(element), driver, Time.HIGH_TIMEOUT, ChronoUnit.SECONDS, true);
  }

  public static void waitForAnimationToFinish() {
    waitSeconds(Time.LOW_TIMEOUT);
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
}
