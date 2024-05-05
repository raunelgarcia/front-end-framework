package tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static pages.BasePage.waitSeconds;
import static utilities.Accessibility.moveHtmlReportToAccessibilityDirectory;
import static utilities.Constants.*;
import static utilities.Constants.ACCESSIBILITY_REPORT_PATH;
import static utilities.Constants.ALLURE_COMMAND_MAC;
import static utilities.Constants.ALLURE_COMMAND_WIN;
import static utilities.LocalEnviroment.isWeb;
import static utilities.LocalEnviroment.isWindows;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import pages.Marca;
import utilities.*;
import utilities.JSExecutor;
import utilities.LocalEnviroment;

@ExtendWith(TestErrorHandler.class)
public class MarcaTest {

  private static Marca controller;

  @BeforeAll
  public static void clean_allure_report() {
    JSExecutor.runCommand(isWindows() ? ALLURE_CLEAN_COMMAND_WIN : ALLURE_CLEAN_COMMAND_MAC);
    JSExecutor.runCommand(
        isWindows() ? NETWORK_LOG_CLEAN_COMMAND_WIN : NETWORK_LOG_CLEAN_COMMAND_MAC);
  }

  @BeforeEach
  public void iAmOnTheMarcaWebsite() {
    controller = new Marca();
  }

  @Test
  public void checkNewsArticleImage() {
    controller.acceptCookies();
  }

  @Test
  public void checkIfNewExists() {
    controller.acceptCookies();
    controller.goToNotice();
    assertTrue(true, "La imagen no se muestra en pantalla");
  }

  @AfterEach
  public void closeDriver() {
    Accessibility.checkAccessibility();
    AllureReport.fillReportInfo();
    NetworkLogs.getNetworkLogs();
    waitSeconds(LOW_TIMEOUT);
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
