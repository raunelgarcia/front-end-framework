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
    controller.acceptCookies();
  }

  @After
  public void closeDriver() {
    // Accessibility.checkAccessibility(driver);
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    driver.quit();
  }
}
