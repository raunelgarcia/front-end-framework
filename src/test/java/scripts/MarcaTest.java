package scripts;

import static helpers.Runners.*;
import static utilities.ExtentReport.*;
import static utilities.FrontEndOperation.checkThat;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import utilities.*;

@ExtendWith(TestErrorHandler.class)
public class MarcaTest {

  private static Marca controller;

  @BeforeAll
  public static void clean_reports_logs() {
    startTest("Marca Test Suite");
    JSExecutor.runCommand(
        LocalEnviroment.isWindows()
            ? Constants.EXTENT_CLEAN_COMMAND_WIN
            : Constants.EXTENT_CLEAN_COMMAND_MAC);
    ExtentReport.fillReportInfo();
    attachEnvironmentInfo(setEnvironmentParameters());
  }

  @BeforeEach
  public void iAmOnTheMarcaWebsite() {
    controller = new Marca();
    startTest(this.getClass().getSimpleName() + " - ".concat(getTestName()));
  }

  @Test
  public void errorWhileLogin() {
    controller.acceptCookies();
    controller.fillLogin();
    checkThat("Visibility of element", controller.visibleMessage(), Matchers.equalTo(true));
  }

  @AfterEach
  public void closeDriver() {
    Accessibility.checkAccessibility();
    NetworkLogs.getNetworkLogs();
    startTest(this.getClass().getName());
  }

  @AfterAll
  public static void runReports() {
    runExtentReport();
    runAccessibilityCopy();
    endTest();
  }

  private String getTestName() {
    return new Throwable().getStackTrace()[1].getMethodName();
  }
}
