package utilities;

import static org.openqa.selenium.support.PageFactory.initElements;
import static utilities.DriverConfiguration.getDriver;

import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Marca extends FrontEndOperation {
  private final WebDriver driver;

  @FindBy(id = "ue-accept-notice-button")
  WebElement acceptCookies;

  @FindBy(css = "a.tools-corporative-link[title='Login']")
  WebElement loginButton;

  @FindBy(id = "email")
  WebElement emailField;

  @FindBy(className = "mdc-button__label")
  WebElement nextButton;

  @FindBy(id = "mat-mdc-error-0")
  private WebElement errorMessage;

  public Marca() {
    driver = getDriver();
    initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(Constants.LOW_TIMEOUT)), this);
  }

  public void acceptCookies() {
    clickWhenVisible(acceptCookies);
  }

  public void fillLogin() {
    clickWhenVisible(loginButton);
    waitForVisibility(emailField);
    emailField.sendKeys("Forcing error...");
    nextButton.click();
  }

  public Boolean visibleMessage() {
    return errorMessage.isDisplayed();
  }
}
