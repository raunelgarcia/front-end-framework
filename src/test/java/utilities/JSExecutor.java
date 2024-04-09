package utilities;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class JSExecutor {
  private final WebDriver driver;

  public JSExecutor(WebDriver driver) {
    this.driver = driver;
  }

  public void executeScript(String script, Object... args) {
    try {
      JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
      Object result = jsExecutor.executeScript(script, args);

      Allure.step("JavaScrip executed correctly", Status.PASSED);
      System.out.println("JavaScrip results: " + result);

    } catch (Exception e) {

      Allure.step("JavaScript Error: " + e.getMessage(), Status.FAILED);
      e.printStackTrace();
    }
  }

  public static void runCommand(String command) {
    String projectDirectory = Paths.get("").toAbsolutePath().toString();
    ArrayList<String> commandArray = new ArrayList<>();
    commandArray.add(command);

    if(LocalEnviroment.isWindows()) {
      commandArray.add(0, "cmd");
      commandArray.add(1, "/c");
    } else {
      commandArray.add(0, "/bin/bash");
      commandArray.add(1, "-c");
    }
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(commandArray);
      processBuilder.directory(new File(projectDirectory));
      processBuilder.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
