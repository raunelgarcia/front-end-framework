package utilities;

import static java.time.Duration.ofMillis;
import static org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT;
import static org.openqa.selenium.interactions.PointerInput.Origin.viewport;
import static utilities.DriverConfiguration.getDriver;
import static utilities.FrontEndOperation.waitForVisibility;
import static utilities.LocalEnviroment.isAndroid;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import java.time.Duration;
import java.util.Collections;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.PointerInput.Kind;
import org.openqa.selenium.interactions.Sequence;

public class W3cActions {

  private static final PointerInput FINGER = new PointerInput(Kind.TOUCH, "finger");


  public static void swipe(AppiumDriver driver, Point start, Point end, int duration) {

    Sequence swipe =
        new Sequence(FINGER, 1)
            .addAction(
                FINGER.createPointerMove(ofMillis(0), viewport(), start.getX(), start.getY()))
            .addAction(FINGER.createPointerDown(LEFT.asArg()))
            .addAction(
                FINGER.createPointerMove(ofMillis(duration), viewport(), end.getX(), end.getY()))
            .addAction(FINGER.createPointerUp(LEFT.asArg()));

    driver.perform(Collections.singletonList(swipe));
  }

  public void tap(WebElement element) {
    waitForVisibility(element);
    Point location = element.getLocation();
    Logger.infoMessage("ELement position: " + location);
    Sequence tap = new Sequence(FINGER, 1);
    tap.addAction(
        FINGER.createPointerMove(
            Duration.ofMillis(0), viewport(), location.x, location.y));
    tap.addAction(FINGER.createPointerDown(LEFT.asArg()));
    tap.addAction(FINGER.createPointerUp(LEFT.asArg()));

    AppiumDriver driver;
    if (isAndroid()) {
      driver = (AndroidDriver) getDriver();
    } else {
      driver = (IOSDriver) getDriver();
    }

    driver.perform(Collections.singletonList(tap));
  }

  protected void tap(int x, int y) {
    Logger.infoMessage("Tap position: (" + x + ", " + y + ")");
    AppiumDriver driver;
    if (isAndroid()) {
      driver = (AndroidDriver) getDriver();
    } else {
      driver = (IOSDriver) getDriver();
    }

    Sequence tap = new Sequence(FINGER, 1);
    tap.addAction(FINGER.createPointerMove(Duration.ofMillis(0), viewport(), x, y));
    tap.addAction(FINGER.createPointerDown(LEFT.asArg()));
    tap.addAction(FINGER.createPointerUp(LEFT.asArg()));

    driver.perform(Collections.singletonList(tap));
  }
}
