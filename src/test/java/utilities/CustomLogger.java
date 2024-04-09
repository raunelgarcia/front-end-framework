package utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomLogger {
  private static final Logger logger = LoggerFactory.getLogger(CustomLogger.class);

  public static void errorMessage(String msg) {
    logger.error(msg);
  }

  public static void infoMessage(String msg) {
    logger.info(msg);
  }
}
