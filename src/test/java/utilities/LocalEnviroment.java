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
    return Objects.nonNull(accessibility) && accessibility.equalsIgnoreCase("true");
  }

  public static String getResolution() {
    return System.getenv("Resolution");
  }

  public static boolean isMobile() {
    String platform = System.getenv("Platform");
    return Objects.equals(platform, "Android");
  }
}
