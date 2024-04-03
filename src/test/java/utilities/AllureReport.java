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

  public static void addComparation(String success, String comparationMessage) {
    descriptionHtml =
        descriptionHtml.concat("<h4>" + success + ": " + comparationMessage + "</h4>");
  }

  public static void attachScreenshot(WebDriver driver) {
    if (driver instanceof TakesScreenshot) {
      byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
      Allure.getLifecycle().addAttachment("Screenshot", "image/png", "png", screenshot);
      Allure.getLifecycle().updateTestCase(testResult -> testResult.setStatus(Status.FAILED));
    }
  }
}
