package utilities;

public class Constants {
  public static final long LOW_TIMEOUT = 2;
  public static final long MEDIUM_TIMEOUT = 4;
  public static final long HIGH_TIMEOUT = 8;
  public static final long SUPER_HIGH_TIMEOUT = 12;
  public static final long PRO_TIMEOUT = 29;
  public static final String GMAIL_DOMAIN = "@gmail.com";
  public static final String HOTMAIL_DOMAIN = "@hotmail.com";
  public static final String LANGUAGE_REGEX = "[a-zA-Z]{2}-[a-zA-Z]{2}";
  public static final String RESOURCE_PATH = "src/test/resources/";
  public static final String ANDROID_CONFIG = "yaml/androidConfiguration.yaml";
  public static final String IOS_CONFIG = "yaml/iOSConfiguration.yaml";
  public static final String WEB_CONFIG = "yaml/webConfiguration.yaml";
  public static final String DRIVER_URL = "http://127.0.0.1:4723";
  public static final String ALLURE_COMMAND_WIN =
      "npx allure generate target/allure-results --clean && npx allure open";
  public static final String ALLURE_COMMAND_MAC =
      "npx allure generate target/allure-results --clean; npx allure open";
  public static final String ACCESSIBILITY_REPORT_PATH = "target/java-a11y/";
  public static final String CUCUMBER_STEPS_PATH = "tests/cucumber_steps";
  public static final String FEATURES_PATH = "src/test/resources/features";
  public static final String ALLOWED_RESOLUTIONS_PATH = "yaml/allowedResolutions.yaml";
}
