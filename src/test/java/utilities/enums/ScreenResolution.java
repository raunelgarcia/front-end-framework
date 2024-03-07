package utilities.enums;

import java.util.Random;
import org.openqa.selenium.Dimension;

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

  public Dimension getDimension() {
    return new Dimension(width, height);
  }

  public static ScreenResolution getRandomResolution() {
    Random random = new Random();
    return values()[random.nextInt(values().length)];
  }
}
