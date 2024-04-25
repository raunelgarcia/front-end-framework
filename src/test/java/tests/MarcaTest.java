package tests;

import static utilities.Accessibility.moveHtmlReportToAccessibilityDirectory;
import static utilities.LocalEnviroment.isWeb;
import static utilities.Constants.ALLURE_COMMAND_WIN;
import static utilities.Constants.ALLURE_COMMAND_MAC;
import static utilities.Constants.ACCESSIBILITY_REPORT_PATH;

import org.junit.Rule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import utilities.JSExecutor;
import utilities.LocalEnviroment;

import static pages.BasePage.waitSeconds;
import static utilities.Constants.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.openqa.selenium.WebDriver;
import pages.Marca;
import utilities.*;

@ExtendWith(TestErrorHandler.class)
public class MarcaTest {

  static WebDriver driver;
  private static Marca controller;

  @BeforeEach
  public void iAmOnTheMarcaWebsite() {
    DriverConfiguration configuration = new DriverConfiguration();
    driver = configuration.getDriver();
    controller = new Marca(driver);
  }

  @Test
  public void checkIfNewExists() {
    controller.acceptCookies();
    controller.goToNotice();
    assertTrue(true, "La imagen no se muestra en pantalla");
  }

  @Test
  public void checkNewsArticleImage() {
    controller.acceptCookies();
  }

  @AfterEach
  public void closeDriver() {
    Accessibility.checkAccessibility(driver);
    AllureReport.fillReportInfo(driver);
    NetworkLogs.getNetworkLogs();
    waitSeconds(LOW_TIMEOUT);
    driver.quit();
  }

  @AfterAll
  public static void runReports() {
    runAllureReport();
    runAccessibilityCopy();
    NetworkLogs.clearLogs();
  }

  public static void runAllureReport() {
    if (LocalEnviroment.isWindows()) {
      JSExecutor.runCommand(ALLURE_COMMAND_WIN);
    } else {
      JSExecutor.runCommand(ALLURE_COMMAND_MAC);
    }
  }

  public static void runAccessibilityCopy() {
    if (LocalEnviroment.getAccessibility() && isWeb()) {
      moveHtmlReportToAccessibilityDirectory(ACCESSIBILITY_REPORT_PATH);
    }
  }
}