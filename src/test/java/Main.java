import org.openqa.selenium.Dimension;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

public class Main {
    public static void main(String[] args) {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--start-maximized");
        EdgeDriver driver = new EdgeDriver(options);
        driver.get("https://www.marca.com");
        driver.quit();
    }
}
