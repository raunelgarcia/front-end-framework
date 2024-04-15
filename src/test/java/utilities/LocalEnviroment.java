package utilities;

import pages.BasePage;

import static utilities.Constants.LANGUAGE_REGEX;

import java.util.Map;
import java.util.Objects;

public class LocalEnviroment {

  public static String getPlatform() {
    return System.getenv("Platform");
  }

  public static String getApplication() {
    return Objects.nonNull(System.getenv("Application"))
        ? System.getenv("Application").toLowerCase()
        : "";
  }

  public static String getBrowser() {
    if (!BasePage.isNullOrEmpty(System.getenv("Browser"))) {
      return System.getenv("Browser").toLowerCase();
    } else {
      return "chrome";
    }
  }

  public static String getResolution() {
    return System.getenv("Resolution");
  }

  public static String getUdid() {
    return System.getenv("Udid");
  }

  public static String getApk() {
    return System.getenv("Apk");
  }

  public static String getAppIdentifier() {
    return System.getenv("AppIdentifier");
  }

  public static String getAppActivity() {
    return System.getenv("AppActivity");
  }

  public static boolean getAccessibility() {
    return Objects.nonNull(System.getenv("Accessibility"))
        && System.getenv("Accessibility").equalsIgnoreCase("true");
  }

  public static boolean isMobile() {
    return !isWeb();
  }

  public static boolean isWeb() {
    return System.getenv("Platform").equalsIgnoreCase("Web");
  }

  public static boolean isAndroid() {
    return System.getenv("Platform").equalsIgnoreCase("Android");
  }

  public static boolean isIOS() {
    return System.getenv("Platform").equalsIgnoreCase("iOS");
  }

  public static boolean isWindows() { return System.getProperty("os.name").toLowerCase().contains("win"); }

  public static boolean isMac() { return System.getProperty("os.name").toLowerCase().contains("mac"); }

  public static String getApplicationUrl() throws IllegalArgumentException {
    Map<String, Map<String, String>> environment = DriverConfiguration.loadCapabilitiesWeb();
    Map<String, String> urls = environment.get("url");
    String url = null;

    if (!urls.containsKey(getApplication()) || getApplication().isBlank()) {
      throw new IllegalArgumentException("Application not found");
    }

    for (Map.Entry<String, String> entry : urls.entrySet()) {
      String application = entry.getKey().toLowerCase();
      String applicationUrl = entry.getValue();
      if (Objects.equals(application, getApplication())) {
        url = applicationUrl;
        break;
      }
    }
    return url;
  }

  public static String getLanguage() {
    String language = System.getenv("Language");

    if (BasePage.isNullOrEmpty(language)) {
      language = "es-ES";
    }

    if (!language.matches(LANGUAGE_REGEX)) {
      throw new IllegalArgumentException("Invalid language format. It should be xx-XX");
    }

    return language;
  }

  public static String getLanguageCode() {
    return getLanguage().split("-")[0];
  }

  public static String getCountryCode() {
    return getLanguage().split("-")[1];
  }
}
