package utilities;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class AllureReport {
  private static String descriptionHtml = setTestDescription();

  public static void fillReportInfo() {
    Allure.descriptionHtml(descriptionHtml);
    descriptionHtml = setTestDescription();
  }

  private static String setTestDescription() {
    StringBuilder description = new StringBuilder();
    description.append("<h3 style=\"text-decoretion: underline;\">Test Enviroment</h3>");
    if (LocalEnviroment.isMobile()) {

    } else {
      description
          .append("<p><b>Platform:</b> ")
          .append(LocalEnviroment.getPlatform())
          .append("</p>");
      description.append("<p><b>Browser:</b> ").append(LocalEnviroment.getBrowser()).append("</p>");
      description
          .append("<p><b>Url:</b> ")
          .append(LocalEnviroment.getApplicationUrl())
          .append("</p>");
      description
          .append("<p><b>Resolution:</b> ")
          .append(ScreenResolution.getResolutionFromEnv())
          .append("</p>");
      description
          .append("<p><b>Accessibility:</b> ")
          .append(LocalEnviroment.getAccessibility())
          .append("</p>");
    }
    return description.toString();
  }

  public static void addComparation(String comparationMessage, boolean success) {
    if (success)
      descriptionHtml =
          descriptionHtml.concat(
              "<h4 style=\"background-color: #97cc64; padding: 8px; color: #fff;\">"
                  + comparationMessage
                  + "</h4>");
    else
      descriptionHtml =
          descriptionHtml.concat(
              "<h4 style=\"background-color: #fd5a3e; padding: 8px; color: #fff;\">"
                  + comparationMessage
                  + "</h4>");
  }

  public static void attachScreenshot(WebDriver driver) {
    if (driver instanceof TakesScreenshot) {
      byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
      Allure.getLifecycle().addAttachment("Screenshot", "image/png", "png", screenshot);
      Allure.getLifecycle().updateTestCase(testResult -> testResult.setStatus(Status.FAILED));
    }
  }
}
