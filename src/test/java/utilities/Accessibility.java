package utilities;

import static utilities.LocalEnviroment.isWeb;

import io.github.sridharbandi.AxeRunner;
import io.github.sridharbandi.HtmlCsRunner;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import org.openqa.selenium.WebDriver;

public class Accessibility {

  public static void checkAccessibility(WebDriver driver) {
    if (LocalEnviroment.getAccessibility() && isWeb()) {
      HtmlCsRunner htmlCsRunner = new HtmlCsRunner(driver);
      try {
        htmlCsRunner.execute();
        htmlCsRunner.generateHtmlReport();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static void checkAccessibilityAxe(WebDriver driver) {
    if (LocalEnviroment.getAccessibility() && isWeb()) {
      AxeRunner axeRunner = new AxeRunner(driver);
      try {
        axeRunner.execute();
        axeRunner.generateHtmlReport();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static void copyDirectory(Path sourceDirectory, Path targetDirectory) throws IOException {
    if (!Files.exists(targetDirectory)) {
      Files.createDirectories(targetDirectory);
    }

    Files.walk(targetDirectory)
        .sorted(Comparator.reverseOrder())
        .map(Path::toFile)
        .forEach(File::delete);

    Files.walk(sourceDirectory)
        .forEach(
            source -> {
              Path target = targetDirectory.resolve(sourceDirectory.relativize(source));
              try {
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
  }

  public static void moveHtmlReportToAccessibilityDirectory(String sourceDirectoryPath) {
    String targetDirectoryPath = "accessibility-report";

    try {
      Path sourceDirectory = new File(sourceDirectoryPath).toPath();
      Path targetDirectory = new File(targetDirectoryPath).toPath();
      copyDirectory(sourceDirectory, targetDirectory);
      Logger.infoMessage(
          "Accessibility report has been successfully copied to 'accessibility-report' directory.");
    } catch (IOException e) {
      Logger.errorMessage("Error copying the accessibility report: " + e.getMessage());
    }
  }
}
