package utilities;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class AllureReport {
  private static final String BASE_DESC = "<h4>Entorno: " + LocalEnviroment.getBrowser() + "</h4>";
  private static String descriptionHtml = BASE_DESC;

  public static void fillReportInfo() {
    Allure.descriptionHtml(descriptionHtml);
    descriptionHtml = BASE_DESC;
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
