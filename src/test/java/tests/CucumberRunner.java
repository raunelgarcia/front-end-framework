package tests;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import org.junit.AfterClass;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features", glue = "tests/cucumber_steps")
public class CucumberRunner {

  @AfterClass
  public static void runAllureReport() {

    String projectDirectory = Paths.get("").toAbsolutePath().toString();
    String[] command;

    String osName = System.getProperty("os.name").toLowerCase();
    if (osName.contains("win")) {
      command = new String[]{"cmd", "/c",
          "npx allure generate target/allure-results --clean && npx allure open"};
    } else {
      command = new String[]{"/bin/bash", "-c",
          "npx allure generate target/allure-results --clean; npx allure open"};
    }

    try {
      ProcessBuilder processBuilder = new ProcessBuilder(command);
      processBuilder.directory(new File(projectDirectory));
      processBuilder.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
