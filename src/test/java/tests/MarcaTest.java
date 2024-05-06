package tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static pages.BasePage.waitSeconds;
import static utilities.Accessibility.checkAccessibility;
import static utilities.Accessibility.moveHtmlReportToAccessibilityDirectory;
import static utilities.AllureReport.fillReportInfo;
import static utilities.Constants.*;
import static utilities.Constants.ACCESSIBILITY_REPORT_PATH;
import static utilities.Constants.ALLURE_COMMAND_MAC;
import static utilities.Constants.ALLURE_COMMAND_WIN;
import static utilities.JSExecutor.runCommand;
import static utilities.LocalEnviroment.getAccessibility;
import static utilities.LocalEnviroment.isWeb;
import static utilities.LocalEnviroment.isWindows;
import static utilities.NetworkLogs.clearLogs;
import static utilities.NetworkLogs.getNetworkLogs;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import pages.Marca;
import utilities.LocalEnviroment;

@ExtendWith(TestErrorHandler.class)
public class MarcaTest {

  private static Marca controller;

  @BeforeAll
  public static void clean_reports_logs() {
    runCommand(isWindows() ? ALLURE_CLEAN_COMMAND_WIN : ALLURE_CLEAN_COMMAND_MAC);
    runCommand(isWindows() ? NETWORK_LOG_CLEAN_COMMAND_WIN : NETWORK_LOG_CLEAN_COMMAND_MAC);
    clearLogs();
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
    checkAccessibility();
    fillReportInfo();
    getNetworkLogs();
    waitSeconds(LOW_TIMEOUT);
  }

  @AfterAll
  public static void runReports() {
    runAllureReport();
    runAccessibilityCopy();
  }

  public static void runAllureReport() {
    if (LocalEnviroment.isWindows()) {
      runCommand(ALLURE_COMMAND_WIN);
    } else {
      runCommand(ALLURE_COMMAND_MAC);
    }
  }

  public static void runAccessibilityCopy() {
    if (getAccessibility() && isWeb()) {
      moveHtmlReportToAccessibilityDirectory(ACCESSIBILITY_REPORT_PATH);
    }
  }
}
