package tests;

import static utilities.Accessibility.moveHtmlReportToAccessibilityDirectory;
import static utilities.LocalEnviroment.isWeb;
import static utilities.Constants.ALLURE_COMMAND_WIN;
import static utilities.Constants.ALLURE_COMMAND_IOS;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
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
    String osName = System.getProperty("os.name").toLowerCase();

    if (osName.contains("win")) {
      JSExecutor.runCommand(ALLURE_COMMAND_WIN);
    } else {
      JSExecutor.runCommand(ALLURE_COMMAND_IOS);
    }

  }

  private static void runAccesibilityCopy() {
    if (LocalEnviroment.getAccessibility() && isWeb()) {
      moveHtmlReportToAccessibilityDirectory("target/java-a11y/");
    }
  }
}
