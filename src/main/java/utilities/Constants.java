package utilities;

import static utilities.LocalEnviroment.getAccessToken;
import static utilities.LocalEnviroment.getUser;

import java.util.Base64;

public class Constants {
  public static final String USER = getUser();
  public static final String ACCESS_TOKEN = getAccessToken();
  public static final String AUTHORIZATION =
      "Basic " + Base64.getEncoder().encodeToString((USER + ":" + ACCESS_TOKEN).getBytes());
  public static final String SAUCELABS_TESTS_URL =
      "https://ondemand.eu-central-1.saucelabs.com:443/wd/hub";
  public static final String SAUCELABS_API_URL = "https://api.eu-central-1.saucelabs.com/";
  public static final String ANDROID_VERSION_REGEX = "(8|9|\\d{2}).*";
  public static final String IOS_VERSION_REGEX = "(1[4-9]|[2-9]\\d).*";

  public static final long LOW_TIMEOUT = 2;
  public static final long MEDIUM_TIMEOUT = 4;
  public static final long HIGH_TIMEOUT = 8;
  public static final long SUPER_HIGH_TIMEOUT = 12;
  public static final long PRO_TIMEOUT = 29;

  public static final String GMAIL_DOMAIN = "@gmail.com";
  public static final String HOTMAIL_DOMAIN = "@hotmail.com";
  public static final String LANGUAGE_REGEX = "[a-zA-Z]{2}-[a-zA-Z]{2}";
  public static final String RESOURCE_PATH = "src/test/resources/";
  public static final String WEB_CONFIG = "yaml/webConfiguration.yaml";
  public static final String DRIVER_URL = "http://127.0.0.1:4723";
  public static final String EXTENT_COMMAND_WIN = "cmd.exe /c mvn clean install ";
  public static final String EXTENT_COMMAND_MAC = "sh -c mvn clean install ";
  public static final String ACCESSIBILITY_REPORT_PATH = "target/java-a11y/";
  public static final String ALLOWED_RESOLUTIONS_PATH = "yaml/allowedResolutions.yaml";
  public static final String EXTENT_CLEAN_COMMAND_WIN = "cmd.exe /c \"rd /s /q .\\target\\extent-reports\\\"";
  public static final String EXTENT_CLEAN_COMMAND_MAC = "rm -rf ./target/extent-reports/";
  public static final String NETWORK_LOG_CLEAN_COMMAND_MAC = "rm -rf ./network-logs/";
  public static final String NETWORK_LOG_CLEAN_COMMAND_WIN = "rd /s /q .\\network-logs\\";
  public static final String NETWORK_LOG_CLEAN_COMMAND_ANDROID = "adb logcat -c";
  public static final String SAUCELABS_SESSION_URL =
      "https://app.eu-central-1.saucelabs.com/tests/";
}
