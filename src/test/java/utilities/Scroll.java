package utilities;

import static java.lang.Math.floor;
import static java.lang.Math.max;
import static strategies.VisibilityStrategy.isVisible;
import static strategies.WaitStrategy.waitForAnimationToFinish;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Scroll {
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
      if (LocalEnviroment.isMobile())
        swipe(direction, 0.4, minScroll ? 0.5 : 0.6, (AppiumDriver) driver);
      else {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,200)");
      }
    }
  }
}
