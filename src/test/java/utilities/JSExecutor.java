package utilities;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class JSExecutor {
  private final WebDriver driver;

  public JSExecutor(WebDriver driver) {
    this.driver = driver;
  }

  public void executeScript(String script, Object... args) {
    try {
      JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
      Object result = jsExecutor.executeScript(script, args);

      Allure.step("Script JavaScript ejecutado correctamente", Status.PASSED);
      System.out.println("Resultado del script JavaScript: " + result);

    } catch (Exception e) {

      Allure.step("Error al ejecutar el script JavaScript: " + e.getMessage(), Status.FAILED);
      e.printStackTrace();
    }
  }
}