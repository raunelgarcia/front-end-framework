package utilities;

import static utilities.Constants.*;
import static utilities.SaucelabsDriverConfiguration.getVerifyDeviceNameExist;

import java.util.Map;
import java.util.Objects;

public class LocalEnviroment {

  protected static String deviceNameValue = getDeviceName();

  public static String getPlatform() {
    return getEnv("Platform");
  }

  public static String getProvider() {
    return getEnv("Provider");
  }

  public static String getApplication() {
    return getEnvOrDefault("Application", "").toLowerCase();
  }

  public static String getBrowser() {
    return getEnvOrDefault("Browser", "chrome").toLowerCase();
  }

  public static String getResolution() {
    return getEnv("Resolution");
  }

  public static String getUdid() {
    return getEnv("Udid");
  }

  public static String getUser() {
    return getEnv("User");
  }

  public static String getAccessToken() {
    return getEnv("AccessToken");
  }

  public static String getAppiumVersion() {
    String appiumVersion = null;
    String platform = getPlatform();

    if (isVirtualDevice() && !FrontEndOperation.isNullOrEmpty(platform)) {
      switch (platform.toLowerCase()) {
        case "android" -> {
          if (checkPlatformVersion(ANDROID_VERSION_REGEX)) {
            appiumVersion = "2.11.0";
          } else {
            Logger.infoMessage(
                "The version you've specified for Android is smaller than 8.0, you should consider using a latest version");
          }
        }
        case "ios" -> {
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
    return getEnv("App");
  }

  public static String getAppIdentifier() {
    return getEnv("AppIdentifier");
  }

  public static String getAppActivity() {
    return getEnv("AppActivity");
  }

  public static boolean getAccessibility() {
    return "true".equalsIgnoreCase(getEnv("Accessibility"));
  }

  public static String getDeviceName() {
    String deviceName = getEnv("DeviceName");
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
    String platformVersion = getEnv("PlatformVersion");
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
    String appVersion = getEnv("AppVersion");
    if (FrontEndOperation.isNullOrEmpty(appVersion)) {
      return "latest";
    }
    return appVersion;
  }

  public static boolean isMobile() {
    return !isWeb();
  }

  public static boolean isWeb() {
    return "Web".equalsIgnoreCase(getPlatform());
  }

  public static boolean isAndroid() {
    return "Android".equalsIgnoreCase(getPlatform());
  }

  public static boolean isIOS() {
    return "iOS".equalsIgnoreCase(getPlatform());
  }

  public static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("win");
  }

  public static boolean isMac() {
    return System.getProperty("os.name").toLowerCase().contains("mac");
  }

  public static boolean isVirtualDevice() {
    String normalizedDeviceName = deviceNameValue.toLowerCase();
    return normalizedDeviceName.contains("emulator") || normalizedDeviceName.contains("simulator");
  }

  public static boolean isSaucelabs() {
    return Objects.nonNull(getProvider()) && getProvider().equalsIgnoreCase("SauceLabs");
  }

  public static boolean checkPlatformVersion(String regex) {
    String platformVersion = getPlatformVersion();
    return (platformVersion.matches(regex)
        || platformVersion.equalsIgnoreCase(".*")
        || platformVersion.equalsIgnoreCase("current_major"));
  }

  public static String getApplicationUrl() throws IllegalArgumentException {
    Map<String, Map<String, String>> environment = DriverConfiguration.loadCapabilitiesWeb();
    Map<String, String> urls = environment.get("url");
    String application = getApplication();

    if (application.isBlank() || !urls.containsKey(application)) {
      throw new IllegalArgumentException("Application not found");
    }

    return urls.get(application);
  }

  public static String getLanguage() {
    String language = getEnv("Language");

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
    String version = getEnv("BrowserVersion");
    if (FrontEndOperation.isNullOrEmpty(version)) {
      return "latest";
    }
    return version.toLowerCase();
  }

  private static String getEnv(String variable) {
    return System.getenv(variable);
  }

  private static String getEnvOrDefault(String variable, String defaultValue) {
    String envValue = getEnv(variable);
    return FrontEndOperation.isNullOrEmpty(envValue) ? defaultValue : envValue;
  }
}
