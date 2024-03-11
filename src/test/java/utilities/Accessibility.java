package utilities;

import io.github.sridharbandi.AxeRunner;
import io.github.sridharbandi.HtmlCsRunner;
import java.io.IOException;
import org.openqa.selenium.WebDriver;

public class Accessibility {

  public static void checkAccessibility(WebDriver driver) {
    if (LocalEnviroment.getAccessibility() && LocalEnviroment.getPlatform()
        .equalsIgnoreCase("Web")) {
      HtmlCsRunner htmlCsRunner = new HtmlCsRunner(driver);
      try {
        htmlCsRunner.execute();
        htmlCsRunner.generateHtmlReport();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static void checkAccessibilityAxe(WebDriver driver) {
    if (LocalEnviroment.getAccessibility() && LocalEnviroment.getPlatform()
        .equalsIgnoreCase("Web")) {
      AxeRunner axeRunner = new AxeRunner(driver);
      try {
        axeRunner.execute();
        axeRunner.generateHtmlReport();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
