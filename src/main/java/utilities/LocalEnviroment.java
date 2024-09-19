package utilities;

import static utilities.Constants.*;
import static utilities.DriverConfiguration.ANSI_CYAN;
import static utilities.SaucelabsDriverConfiguration.getVerifyDeviceNameExist;

import java.util.Map;
import java.util.Objects;

public class LocalEnviroment {

  public static final String ANSI_RED = "\u001B[31m";
  public static final String ANSI_RESET = "\u001B[0m";

  protected static String deviceNameValue = getDeviceName();

  public static String getPlatform() {
    return System.getenv("Platform");
  }

  public static String getProvider() {
    return System.getenv("Provider");
  }

  public static String getApplication() {
    return Objects.nonNull(System.getenv("Application"))
        ? System.getenv("Application").toLowerCase()
        : "";
  }

  public static String getBrowser() {
    if (!FrontEndOperation.isNullOrEmpty(System.getenv("Browser"))) {
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

  public static String getUser() {
    return System.getenv("User");
  }

  public static String getAccessToken() {
    return System.getenv("AccessToken");
  }

  public static final String ANSI_YELLOW = "\u001B[33m";

  public static String getAppiumVersion() {
    Logger.infoMessage(ANSI_CYAN + "Estoy en la función getAppiumVersion()" + ANSI_RESET);

    String appiumVersion = null;
    Logger.infoMessage(ANSI_YELLOW + "Antes de la condiccion isVirtualDevice()" + ANSI_RESET);
    if (isVirtualDevice()) {
      Logger.infoMessage(ANSI_YELLOW + "Antes del switch isVirtualDevice()" + ANSI_RESET);
      switch (getPlatform().toLowerCase()) {
        case "android" -> {
          Logger.infoMessage(
              ANSI_YELLOW
                  + "Antes de la condiccion checkPlatformVersion(ANDROID_VERSION_REGEX)"
                  + ANSI_RESET);
          if (checkPlatformVersion(ANDROID_VERSION_REGEX)) {
            appiumVersion = "2.11.0";
          } else {
            Logger.infoMessage(
                "The version you've specified for Android is smaller than 8.0, you should consider using a latest version");
          }
        }
        case "ios" -> {
          Logger.infoMessage(
              ANSI_YELLOW
                  + "Antes de la condiccion checkPlatformVersion(IOS_VERSION_REGEX)"
                  + ANSI_RESET);

          if (checkPlatformVersion(IOS_VERSION_REGEX)) {
            appiumVersion = "2.0.0";
          } else {
            Logger.infoMessage(
                "The version you've specified for iOS is smaller than 14.0, you should consider using a latest version");
          }
        }
      }
    } else {
      appiumVersion = "latest";
    }
    return appiumVersion;
  }

  public static String getApp() {
    return System.getenv("App");
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

  public static String getDeviceName() {
    String deviceName = System.getenv("DeviceName");
    Logger.infoMessage(ANSI_RED + "Estoy en la función getDeviceName()" + ANSI_RESET);
    if (FrontEndOperation.isNullOrEmpty(deviceName)) {
      return ".*";
    } else {
      switch (deviceName.toLowerCase()) {
        case "emulator" -> {
          if (isAndroid()) {
            deviceName = "Android GoogleAPI Emulator";
            Logger.infoMessage("Device name is: " + deviceName);
          } else {
            Logger.errorMessage(
                "Check your capabilities DeviceName and Platform are emulator and Android, respectively");
          }
        }
        case "simulator" -> {
          if (isIOS()) {
            deviceName = "iPhone Simulator";
            Logger.infoMessage("Device name is: " + deviceName);
          } else {
            Logger.errorMessage(
                "Check your capabilities DeviceName and Platform are simulator and iOS, respectively");
          }
        }
        default -> {
          if (getVerifyDeviceNameExist(Constants.AUTHORIZATION, deviceName)) {
            Logger.infoMessage("Device name is: " + deviceName);
          }
        }
      }
    }
    return deviceName;
  }

  public static String getPlatformVersion() {
    String platformVersion = System.getenv("PlatformVersion");
    if (FrontEndOperation.isNullOrEmpty(platformVersion)) {
      if (isVirtualDevice()) {
        platformVersion = "current_major";
      } else {
        if (isAndroid()) {
          platformVersion = ANDROID_VERSION_REGEX;
        } else {
          platformVersion = IOS_VERSION_REGEX;
        }
      }
    }
    return platformVersion;
  }

  public static String getAppVersion() {
    String appVersion = System.getenv("AppVersion");
    if (FrontEndOperation.isNullOrEmpty(appVersion)) {
      return "latest";
    }
    return appVersion;
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

  public static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("win");
  }

  public static boolean isMac() {
    return System.getProperty("os.name").toLowerCase().contains("mac");
  }

  public static boolean isVirtualDevice() {
    return (deviceNameValue.contains("Emulator") || deviceNameValue.contains("Simulator"));
  }

  public static boolean isSaucelabs() {
    return Objects.nonNull(getProvider()) && getProvider().equalsIgnoreCase("SauceLabs");
  }

  public static boolean checkPlatformVersion(String regex) {
    return (getPlatformVersion().matches(regex)
        || getPlatformVersion().equalsIgnoreCase(".*")
        || getPlatformVersion().equalsIgnoreCase("current_major"));
  }

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

    if (FrontEndOperation.isNullOrEmpty(language)) {
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

  public static String getBrowserVersion() {
    String version = System.getenv("BrowserVersion");
    if (FrontEndOperation.isNullOrEmpty(version)) {
      return "latest";
    }
    return version.toLowerCase();
  }
}
