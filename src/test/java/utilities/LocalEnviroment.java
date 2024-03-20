package utilities;

import java.util.Objects;

public class LocalEnviroment {

  static final String platform = System.getenv("Platform");

  static final String url = System.getenv("Url");

  static final String browser = System.getenv("Browser");

  static final String resolution = System.getenv("Resolution");

  public static String getPlatform() {
    return platform;
  }

  public static String getUrl() {
    return url;
  }

  public static String getBrowser() {
    if (Objects.nonNull(browser)) {
      return browser.toLowerCase();
    } else {
      return "chrome";
    }
  }

  public static String getResolution() {
    return resolution;
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

  public static boolean getAccessibility() {
    String accessibility = System.getenv("Accessibility");
    return Objects.nonNull(accessibility) && accessibility.equalsIgnoreCase("true");
  }

  public static boolean isMobile() {
    String platform = System.getenv("Platform");
    return Objects.nonNull(platform) && platform.equalsIgnoreCase("Android")
        || platform.equalsIgnoreCase("IOS");
  }
}
