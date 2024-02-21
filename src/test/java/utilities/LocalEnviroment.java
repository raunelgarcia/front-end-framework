package utilities;

public class LocalEnviroment {
  public static String getPlatform() {
    return System.getenv("Platform");
  }

  public static String getUrl() {
    return System.getenv("Url");
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
    return accessibility != null && accessibility.equalsIgnoreCase("true");
  }
}
