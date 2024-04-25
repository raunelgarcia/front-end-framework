package tests;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import utilities.AllureReport;

public class TestErrorHandler implements TestWatcher, AfterAllCallback {

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        AllureReport.attachScreenshot(driver);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {

    }
}
