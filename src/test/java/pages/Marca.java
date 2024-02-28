package pages;

import static strategies.VisibilityStrategy.isVisible;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import utilities.Direction;
import utilities.Scroll;

public class Marca {
  @FindBy(id = "ue-accept-notice-button")
  WebElement acceptCookies;

  @FindBy(partialLinkText = "La UFC acelera su llegada a España")
  @AndroidFindBy(
      xpath =
          "(//android.widget.LinearLayout[@resource-id=\"com.iphonedroid.marca:id/portadilla_container\"])[3]")
  WebElement randomNotice;

  private final WebDriver driver;

  public Marca(WebDriver driver) {
    this.driver = driver;
    this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
    PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(1)), this);
  }

  public void sampleTest() {
    if (isVisible(acceptCookies)) acceptCookies.click();
    Scroll.scrollToElement(randomNotice, Direction.UP, false, driver);
    System.out.println("La noticia se ve? " + isVisible(randomNotice));
    //randomNotice.click();
  }
}
