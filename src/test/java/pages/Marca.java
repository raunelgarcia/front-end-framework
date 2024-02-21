package pages;

import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class Marca {
  @FindBy(id = "ue-accept-notice-button")
  WebElement acceptCookies;

  @FindBy(css = "li.main-tab.main-tab-futbol")
  WebElement footballButton;

  @FindBy(css = "a.tools-corporative-link.js-loginClick")
  WebElement loginButton;

  private final WebDriver driver;

  public Marca(WebDriver driver) {
    this.driver = driver;
    this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
  }

  public void firstTest() {
    acceptCookies.click();
  }
}
