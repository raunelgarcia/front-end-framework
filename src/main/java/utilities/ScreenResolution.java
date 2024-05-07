package utilities;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.openqa.selenium.Dimension;
import org.yaml.snakeyaml.Yaml;

public class ScreenResolution {

  private ScreenResolution() {
    throw new AssertionError("This class should not be instantiated.");
  }

  public static Dimension getResolutionFromEnv() throws IllegalStateException {
    String browser = LocalEnviroment.getBrowser().toLowerCase();
    String envResolution = LocalEnviroment.getResolution();

    if (BasePage.isNullOrEmpty(envResolution)) {
      return new Dimension(1024, 768);
    }

    String[] envResolutionComponents = envResolution.split("x");

    if (envResolutionComponents.length != 2) {
      throw new IllegalStateException("Invalid resolution format");
    }

    Dimension resolution =
        new Dimension(
            Integer.parseInt(envResolutionComponents[0]),
            Integer.parseInt(envResolutionComponents[1]));

    if (!isValidResolution(envResolution, browser)) {
      throw new IllegalStateException(
          "The resolution specified by the \"Resolution\" environment variable is not "
              + "valid for the browser specified by the \"Browser\" environment variable");
    }

    return resolution;
  }

  public static boolean isValidResolution(String resolution, String browser)
      throws IllegalStateException {
    Map<String, Map<String, Map<String, List<String>>>> allowableResolutions =
        loadAllowedResolutions();

    if (Objects.isNull(allowableResolutions)) {
      throw new IllegalStateException(
          "The \"allowedResolutions.yaml\" file could not be located or loaded");
    }

    return allowableResolutions
        .get("browsers")
        .get(browser)
        .get("resolutions")
        .contains(resolution);
  }

  private static Map<String, Map<String, Map<String, List<String>>>> loadAllowedResolutions() {
    Yaml yaml = new Yaml();
    try (InputStream inputStream =
        ScreenResolution.class
            .getClassLoader()
            .getResourceAsStream(Constants.ALLOWED_RESOLUTIONS_PATH)) {
      return yaml.load(inputStream);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load or parse the YAML file", e);
    }
  }
}
