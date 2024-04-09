package tests;

import static utilities.Accessibility.moveHtmlReportToAccessibilityDirectory;
import static utilities.LocalEnviroment.isWeb;
import static utilities.Constants.ALLURE_COMMAND_WIN;
import static utilities.Constants.ALLURE_COMMAND_MAC;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import utilities.JSExecutor;
import utilities.LocalEnviroment;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features", glue = "tests/cucumber_steps")
public class CucumberRunner {

  @AfterClass
  public static void runReports() {
    runAllureReport();
    runAccesibilityCopy();
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
      moveHtmlReportToAccessibilityDirectory("target/java-a11y/");
    }
  }
}
