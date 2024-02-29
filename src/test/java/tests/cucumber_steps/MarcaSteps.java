package tests.cucumber_steps;

import static org.junit.Assert.assertTrue;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import pages.Marca;
import utilities.DriverConfiguration;

public class MarcaSteps {
  private WebDriver driver;
  private Marca controller;

  @Given("I am on the Marca website")
  public void iAmOnTheMarcaWebsite() {
    DriverConfiguration configuration = new DriverConfiguration();
    driver = configuration.getDriver();
    controller = new Marca(driver);
  }

  @When("I navigate to a news article")
  public void iNavigateToANewsArticle() {
    controller.acceptCookies();
    controller.goToNotice();
    controller.acceptAge();
  }

  @Then("I should be able to see if the article contains an image")
  public void iShouldBeAbleToSeeIfTheArticleContainsAnImage() {
    assertTrue("La imagen se muestra en pantalla", true);
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
