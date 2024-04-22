package tests;

import static utilities.Accessibility.moveHtmlReportToAccessibilityDirectory;
import static utilities.Constants.ACCESSIBILITY_REPORT_PATH;
import static utilities.Constants.ALLURE_COMMAND_MAC;
import static utilities.Constants.ALLURE_COMMAND_WIN;
import static utilities.Constants.CUCUMBER_STEPS_PATH;
import static utilities.Constants.FEATURES_PATH;
import static utilities.LocalEnviroment.isWeb;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import utilities.JSExecutor;
import utilities.LocalEnviroment;
import utilities.NetworkLogs;

@RunWith(Cucumber.class)
@CucumberOptions(features = FEATURES_PATH, glue = CUCUMBER_STEPS_PATH)
public class CucumberRunner {

  @AfterClass
  public static void runReports() {
    runAllureReport();
    runAccesibilityCopy();
    NetworkLogs.clearLogs();
  }

  private static void runAllureReport() {
    if (LocalEnviroment.isWindows()) {
      JSExecutor.runCommand(ALLURE_COMMAND_WIN);
    } else {
      JSExecutor.runCommand(ALLURE_COMMAND_MAC);
    }
  }

  private static void runAccesibilityCopy() {
    if (LocalEnviroment.getAccessibility() && isWeb()) {
      moveHtmlReportToAccessibilityDirectory(ACCESSIBILITY_REPORT_PATH);
    }
  }
}
