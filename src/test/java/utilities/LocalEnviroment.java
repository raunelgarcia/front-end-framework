package utilities;

import org.openqa.selenium.Dimension;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class LocalEnviroment {

  private static URL url = null;
  static {
    try {
      url = new URL(System.getenv("Url"));
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  private static final String browser = (Objects.nonNull(System.getenv("Browser")))
          ? LocalEnviroment.getBrowser().toLowerCase()
          : "chrome";

  private static final boolean accesibility = Objects.nonNull(System.getenv("Accessibility"))
          && System.getenv("Accessibility").equalsIgnoreCase("true");

  private static final Dimension resolution;
  static {
    String[] envResolutionComponents = System.getenv("Resolution").split("x");
    resolution = new Dimension(Integer.parseInt(envResolutionComponents[0]),
            Integer.parseInt(envResolutionComponents[1]));
  }

  public LocalEnviroment() throws AssertionError {
    throw new AssertionError("This class cannot be instantiated");
  }

  public static String getPlatform() {
    return System.getenv("Platform");
  }

  public static URL getUrl() {
    return url;
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
    return browser;
  }

  public static boolean getAccessibility() {
    return accesibility;
  }

  public static Dimension getResolution() {
    return resolution;
  }

  public static boolean isMobile() {
    String platform = System.getenv("Platform");
    return Objects.nonNull(platform) && platform.equalsIgnoreCase("Android")
            || platform.equalsIgnoreCase("IOS");
  }
}







