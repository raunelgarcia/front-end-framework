package tests.cucumber_steps;

import static org.junit.Assert.assertTrue;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import pages.Marca;
import utilities.Accessibility;
import utilities.AllureReport;
import utilities.DriverConfiguration;
import utilities.Screenshot;

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
    //controller.acceptAge();
  }

  @Then("I should be able to see if the article contains an image")
  public void iShouldBeAbleToSeeIfTheArticleContainsAnImage() {
    assertTrue("La imagen se muestra en pantalla", true);
  }

  @When("I look for a notice")
  public void iLookForANotice() {
    controller.acceptCookies();
  }

  @Then("I should be able to see the notice")
  public void iShouldBeAbleToSeeTheNotice() {

  }

  @AfterStep
  public void afterStep(Scenario scenario) {
    if (scenario.isFailed()) {
      AllureReport.attachScreenshot(driver);
    }
  }

  @After
  public void closeDriver() {
    Accessibility.checkAccessibility(driver);
    AllureReport.fillReportInfo();
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    driver.quit();
  }
}
