package tests;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import org.junit.AfterClass;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "tests/cucumber_steps"
)
public class CucumberRunner {

  @AfterClass
  public static void runAllureReport() {
    try {
      String projectDirectory = Paths.get("").toAbsolutePath().toString();
      ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c",
          "npx allure-commandline generate target/allure-results --clean && npx allure-commandline open allure-report");
      processBuilder.directory(new File(projectDirectory));
      processBuilder.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
