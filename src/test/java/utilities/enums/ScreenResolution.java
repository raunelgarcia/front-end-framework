package utilities.enums;

import java.util.Random;
import org.openqa.selenium.Dimension;
import utilities.LocalEnviroment;

public enum ScreenResolution {
  TABLET(768, 1024),
  HD(1280, 720),
  FULL_HD(1920, 1080),
  FOUR_K(3840, 2160);

  public final int width;
  public final int height;

  ScreenResolution(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public static Dimension setResolution() {
    String dimensionString = LocalEnviroment.getResolution();
    if (dimensionString != null && !dimensionString.isEmpty()) {
      String[] dimensionParts = dimensionString.split("x");
      if (dimensionParts.length == 2) {
        try {
          int width = Integer.parseInt(dimensionParts[0]);
          int height = Integer.parseInt(dimensionParts[1]);
          return new Dimension(width, height);
        } catch (NumberFormatException e) {
          System.err.println(
              "Error parsing screen resolution from environment variable. Using random resolution.");
        }
      }
    }
    return getRandomResolution().getDimension();
  }

  public Dimension getDimension() {
    return new Dimension(width, height);
  }

  public static ScreenResolution getRandomResolution() {
    Random random = new Random();
    return values()[random.nextInt(values().length)];
  }
}
