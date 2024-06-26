package utilities;

import static utilities.LocalEnviroment.isAndroid;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class AllureReport {
  private static String descriptionHtml = "";
  private static String checksHtml = "";

  public static void fillReportInfo() {
    descriptionHtml = setTestDescription();
    Allure.descriptionHtml(descriptionHtml);
    descriptionHtml = "";
  }

  private static String setTestDescription() {
    WebDriver driver = DriverConfiguration.getDriver();
    StringBuilder description = new StringBuilder();
    AppiumDriver driverMobile;
    String os = null;
    description.append("<h3 style=\"text-decoration: underline;\">Test Enviroment</h3>");
    description.append("<p><b>Platform:</b> ").append(LocalEnviroment.getPlatform()).append("</p>");
    description.append("<p><b>Language:</b> ").append(LocalEnviroment.getLanguage()).append("</p>");
    if (LocalEnviroment.isMobile()) {
      if (LocalEnviroment.isAndroid()) {
        driverMobile = (AndroidDriver) driver;
        String appActivity = LocalEnviroment.getAppActivity();
        String deviceName = driverMobile.getCapabilities().getCapability("deviceName").toString();
        description.append("<p><b>Device Name:</b> ").append(deviceName).append("</p>");
        String platformVersion =
            driverMobile.getCapabilities().getCapability("platformVersion").toString();

        description
            .append("<p><b>Platform Version:</b>".concat(isAndroid() ? "Android " : "IOS"))
            .append(platformVersion)
            .append("</p>");
        if (!FrontEndOperation.isNullOrEmpty(appActivity)) {
          description.append("<p><b>App Activity:</b> ").append(appActivity).append("</p>");
        }
      } else {
        driverMobile = (IOSDriver) driver;
      }
      description
          .append("<p><b>Udid:</b> ")
          .append(
              driverMobile
                  .getCapabilities()
                  .getCapability(isAndroid() ? "deviceUDID" : "udid")
                  .toString())
          .append("</p>");
      description
          .append("<p><b>App Identifier:</b> ")
          .append(LocalEnviroment.getAppIdentifier())
          .append("</p>");
      String apk = LocalEnviroment.getApp();
      if (!FrontEndOperation.isNullOrEmpty(apk)) {
        description.append("<p><b>App:</b> ").append(apk).append("</p>");
      }
    } else {
      if (LocalEnviroment.isWindows()) {
        os = "Windows";
      } else if (LocalEnviroment.isMac()) {
        os = "Mac";
      } else {
        os = "linux";
      }
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
      description.append("<p><b>Operating System:</b> ").append(os).append("</p>");
    }
    description.append(checksHtml);
    checksHtml = "";
    return description.toString();
  }

  public static void addComparation(String comparationMessage, boolean success) {
    if (success)
      checksHtml =
          checksHtml.concat(
              "<h4 style=\"background-color: #97cc64; padding: 8px; color: #fff;\">"
                  + comparationMessage
                  + "</h4>");
    else
      checksHtml =
          checksHtml.concat(
              "<h4 style=\"background-color: #fd5a3e; padding: 8px; color: #fff;\">"
                  + comparationMessage
                  + "</h4>");
  }

  public static void attachTextFileToAllureReport(File file) {
    try {
      byte[] content = Files.readAllBytes(Paths.get(file.toURI()));
      Allure.addAttachment("NetworkLogs", "text/plain", new String(content));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void attachScreenshot(WebDriver driver) {
    if (driver instanceof TakesScreenshot) {
      byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
      Allure.getLifecycle().addAttachment("Screenshot", "image/png", "png", screenshot);
      Allure.getLifecycle().updateTestCase(testResult -> testResult.setStatus(Status.FAILED));
    }
  }
}
