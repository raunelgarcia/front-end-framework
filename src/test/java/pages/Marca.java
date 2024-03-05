package pages;

import static strategies.VisibilityStrategy.isVisible;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class Marca {

  @FindBy(id = "ue-accept-notice-button")
  WebElement acceptCookies;

  @FindBy(partialLinkText = "La crisis de Alpine")
  @AndroidFindBy(
      xpath =
          "(//android.widget.LinearLayout[@resource-id=\"com.iphonedroid.marca:id/portadilla_container\"])[3]")
  WebElement randomNotice;

  @FindBy(id = "buttonYes")
  WebElement ageButton;

  private final WebDriver driver;

  public Marca(WebDriver driver) {
    this.driver = driver;
    this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
    PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(1)), this);
  }

  public void acceptCookies() {
    if (isVisible(acceptCookies)) {
      acceptCookies.click();
    }
  }

  public void goToNotice() {
    randomNotice.click();
  }

  public void acceptAge() {
    if (isVisible(ageButton)) {
      ageButton.click();
    }
  }

  public boolean isNoticeShow() {
    return isVisible(randomNotice);
  }
}
