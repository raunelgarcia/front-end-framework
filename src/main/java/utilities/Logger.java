package utilities;

import org.slf4j.LoggerFactory;

public class Logger {
  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Logger.class);

  public static void errorMessage(String msg) {
    logger.error(msg);
  }

  public static void infoMessage(String msg) {
    logger.info(msg);
  }
}
