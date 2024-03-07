package utilities;

import static utilities.enums.ScreenResolution.getRandomResolution;

import java.util.Objects;
import org.openqa.selenium.Dimension;

public class LocalEnviroment {

  public static String getPlatform() {
    return System.getenv("Platform");
  }

  public static String getUrl() {
    return System.getenv("Url");
  }

  public static String getUdid() {
    return System.getenv("Udid");
  }

  public static String getApk() {
    return System.getenv("Apk");
  }

  public static String getAppPackage() {
    return System.getenv("AppPackage");
  }

  public static String getAppActivity() {
    return System.getenv("AppActivity");
  }

  public static String getBrowser() {
    return System.getenv("Browser");
  }

  public static boolean getAccessibility() {
    String accessibility = System.getenv("Accessibility");
    return accessibility != null && accessibility.equalsIgnoreCase("true");
  }

  public static Dimension getResolution() {
    String dimensionString = System.getenv("Resolution");
    if (dimensionString != null && !dimensionString.isEmpty()) {
      String[] dimensionParts = dimensionString.split("x");
      if (dimensionParts.length == 2) {
        try {
          int width = Integer.parseInt(dimensionParts[0]);
          int height = Integer.parseInt(dimensionParts[1]);
          return new Dimension(width, height);
        } catch (NumberFormatException e) {
          System.err.println("Error parsing screen resolution from environment variable. Using random resolution.");
        }
      }
    }
    // If environment variable is not defined or invalid, return a random resolution
    return getRandomResolution().getDimension();
  }

  public static boolean isMobile() {
    String platform = System.getenv("Platform");
    return Objects.equals(platform, "Android");
  }
}
