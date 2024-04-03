package utilities;

import java.nio.file.Paths;

public class Constants {
  public static final long LOW_TIMEOUT = 2;
  public static final long MEDIUM_TIMEOUT = 4;
  public static final long HIGH_TIMEOUT = 8;
  public static final long SUPER_HIGH_TIMEOUT = 12;
  public static final long PRO_TIMEOUT = 29;
  public static final String GMAIL_DOMAIN = "@gmail.com";
  public static final String HOTMAIL_DOMAIN = "@hotmail.com";
  public static final String LANGUAGE_REGEX = "[a-zA-Z]{2}-[a-zA-Z]{2}";
  public static final String RESOURCE_PATH = Paths.get("src/test/resources/" + LocalEnviroment.getApk()).toAbsolutePath().toString();

}