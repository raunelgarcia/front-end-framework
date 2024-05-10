package utilities;

import static java.time.Duration.ofMillis;
import static org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT;
import static org.openqa.selenium.interactions.PointerInput.Origin.viewport;

import io.appium.java_client.AppiumDriver;

import java.util.Arrays;
import java.util.Collections;

import io.appium.java_client.android.AndroidDriver;
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

  public static void tap(WebElement element) {
    Point location = element.getLocation();
    Logger.infoMessage("Posicion del elemento: " + location);
    tap(location.getX(), location.getY());
  }

  public static void tap(int x, int y) {
    AndroidDriver driver = (AndroidDriver) DriverConfiguration.getDriver();

    Sequence tap = new Sequence(FINGER, 1);
    tap.addAction(FINGER.createPointerMove(ofMillis(0), PointerInput.Origin.viewport(), x, y));

    driver.perform(Collections.singletonList(tap));
  }
}
