package utilities;

import java.util.Locale;
import java.util.ResourceBundle;

public class Text {
  private static final String BASE_NAME = "texts.texts";

  public static String get(String key) {
    String language = LocalEnviroment.getLanguage();
    language = language.replace("-", "_");
    ResourceBundle resourceBundle = ResourceBundle.getBundle(BASE_NAME, new Locale(language));
    return resourceBundle.getString(key);
  }
}
