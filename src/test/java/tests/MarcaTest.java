package tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import pages.Marca;
import utilities.Accessibility;
import utilities.DriverConfiguration;

public class MarcaTest {
  private WebDriver driver;

  @Before
  public void configureDriver() {
    DriverConfiguration configuration = new DriverConfiguration();
    driver = configuration.getDriver();
  }

  @Test
  public void demoTest() {
    Marca controller = new Marca(driver);
    controller.firstTest();
  }

  @After
  public void closeDriver() {
    driver.get("https://es.react.dev/");
    Accessibility.checkAccessibility(driver);
  }
}
