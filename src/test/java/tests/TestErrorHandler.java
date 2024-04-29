package tests;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import utilities.AllureReport;
import utilities.DriverConfiguration;

public class TestErrorHandler implements TestWatcher, AfterAllCallback {

  @Override
  public void testFailed(ExtensionContext context, Throwable cause) {
    AllureReport.attachScreenshot(DriverConfiguration.getDriver());
    cleanUp();
  }

  @Override
  public void testSuccessful(ExtensionContext context) {
    cleanUp();
  }

  void cleanUp() {
    DriverConfiguration.quitDriver();
  }

  @Override
  public void afterAll(ExtensionContext extensionContext) throws Exception {}
}
