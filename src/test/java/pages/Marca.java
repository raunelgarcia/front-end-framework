package pages;

import static org.hamcrest.Matchers.containsString;
import static utilities.Constants.LOW_TIMEOUT;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class Marca extends BasePage {

  @FindBy(id = "ue-accept-notice-button")
  WebElement acceptCookies;

  @FindBy(partialLinkText = "Noticia que no existe")
  @AndroidFindBy(
      xpath =
          "(//android.widget.LinearLayout[@resource-id=\"com.iphonedroid.marca:id/portadilla_container\"])[3]")
  WebElement randomNotice;

  @FindBy(id = "buttonYes")
  WebElement ageButton;

  private final WebDriver driver;

  public Marca(WebDriver driver) {
    this.driver = driver;
    PageFactory.initElements(
        new AppiumFieldDecorator(driver, Duration.ofSeconds(LOW_TIMEOUT)), this);
  }

  public void acceptCookies() {
    if (isVisible(acceptCookies)) {
      acceptCookies.click();
    }
  }

  public void goToNotice() {
    String str = "Hello, world!";
    checkThat("the string", str, containsString("fail"));
    // randomNotice.click();
  }

  public void acceptAge() {
    clickWhenVisible(ageButton, driver);
  }

  public boolean isNoticeShow() {
    return isVisible(randomNotice);
  }
}
