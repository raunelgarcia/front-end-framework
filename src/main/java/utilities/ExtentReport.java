package utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static utilities.Constants.SAUCELABS_SESSION_URL;
import static utilities.DriverConfiguration.SLsession;
import static utilities.DriverConfiguration.getDriver;
import static utilities.LocalEnviroment.*;

public class ExtentReport {
    private static final RemoteWebDriver driver = (RemoteWebDriver) getDriver();
    private static final Capabilities capabilities = driver.getCapabilities();
    private static final ExtentReports extent = new ExtentReports();
    private static ExtentTest mainTest;
    private static final StringBuilder checks = new StringBuilder();

    public static void startTest(String testName) {
        mainTest = extent.createTest(testName);
    }

    public static void createStep(String stepName, String stepDescription, Status status) {
        ExtentTest step = mainTest.createNode(stepName);
        step.log(status, stepDescription);
    }

    public static void attachEnvironmentInfo(ImmutableMap<String, String> environmentValuesSet) {
        ExtentTest envTest = mainTest.createNode("Environment Variables");
        environmentValuesSet.forEach((key, value) -> envTest.log(Status.INFO, key + ": " + value));
    }

    public static ImmutableMap<String, String> setEnvironmentParameters() {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        builder.put("Accessibility", String.valueOf(LocalEnviroment.getAccessibility()));
        String platformName = capabilities.getCapability("platformName").toString();
        builder.put("Platform", platformName);
        builder.put("Provider", LocalEnviroment.getProvider());

        if (LocalEnviroment.isSaucelabs()) {
            addSauceLabsParameters(builder);
        } else {
            switch (platformName.toLowerCase()) {
                case "android" -> addAndroidParameters(builder);
                case "ios" -> addIosParameters(builder);
                default -> addWebParameters(builder);
            }
        }
        return builder.build();
    }

    private static void addWebParameters(ImmutableMap.Builder<String, String> builder) {
        builder.put("Application", DriverConfiguration.setURL())
                .put("Browser", capabilities.getCapability("browserName") + " (" +
                        ((RemoteWebDriver) getDriver()).getCapabilities().getBrowserVersion() + ")")
                .put("Resolution", driver.manage().window().getSize().toString());
    }

    // Adds Sauce Labs-specific parameters
    private static void addSauceLabsParameters(ImmutableMap.Builder<String, String> builder) {
        builder.put("SauceLabs test session", SAUCELABS_SESSION_URL.concat(SLsession));
        if (LocalEnviroment.isMobile()) {
            builder.put("App", getApp());
            builder.put("AppIdentifier", getAppIdentifier())
                    .put("AppVersion", SaucelabsDriverConfiguration.appVersion)
                    .put("DeviceName", getDeviceName())
                    .put("PlatformVersion", (String) driver.getCapabilities().getCapability("appium:platformVersion"))
                    .put("Udid", getUdid());
        } else {
            addWebParameters(builder);
        }
    }

    // Adds Android-specific parameters
    private static void addAndroidParameters(ImmutableMap.Builder<String, String> builder) {
        builder.put("Udid", getUdid());
        String appActivity = (String) capabilities.getCapability("appium:appActivity");
        builder.put("AppActivity", appActivity).put("AppIdentifier", getAppIdentifier());
        builder.put("App", getApp());
    }

    // Adds iOS-specific parameters
    private static void addIosParameters(ImmutableMap.Builder<String, String> builder) {
        builder.put("Udid", getUdid()).put("AppIdentifier", getAppIdentifier());
    }

    // Attaches network log file to the report
    public static void attachTextFileToExtentReport(File file) {
        ExtentTest networkLogTest = mainTest.createNode("Network Logs");
        try {
            String content = new String(java.nio.file.Files.readAllBytes(Paths.get(file.toURI())));
            networkLogTest.log(Status.INFO, "Network Log Content:\n" + content);
        } catch (IOException e) {
            networkLogTest.log(Status.FAIL, "Failed to attach network logs: " + e.getMessage());
        }
    }

    // Attaches screenshot to the report
    public static void attachScreenshot(WebDriver driver) {
        ExtentTest screenshotTest = mainTest.createNode("Screenshots");
        if (driver instanceof TakesScreenshot) {
            String base64Screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
            screenshotTest.log(Status.FAIL, "Screenshot on Failure")
                    .addScreenCaptureFromBase64String(base64Screenshot);
        } else {
            screenshotTest.log(Status.WARNING, "Driver does not support screenshots");
        }
    }

    // Adds comparison result with styled background color based on success or failure
    public static void addComparation(String comparationMessage, boolean success) {
        checks.append("<h4 style='background-color: ")
                .append(success ? "#97cc64" : "#fd5a3e")
                .append("; padding: 8px; color: #fff;'>")
                .append(comparationMessage)
                .append("</h4>");
        ExtentTest comparisonNode = mainTest.createNode("Comparison Results");
        comparisonNode.log(success ? Status.PASS : Status.FAIL, comparationMessage);
    }

    public static void fillReportInfo() {
        String platformName = capabilities.getCapability("platformName").toString();
        String language = LocalEnviroment.getLanguage();
        ExtentTest environmentNode = mainTest.createNode("Test Environment");
        environmentNode.log(Status.INFO, "Platform: " + platformName);
        environmentNode.log(Status.INFO, "Language: " + language);

        if (LocalEnviroment.isMobile()) {
            String platformVersion = (String) capabilities.getCapability("appium:platformVersion");
            environmentNode.log(Status.INFO, "Device Name: " + getDeviceName());
            environmentNode.log(Status.INFO, "Platform Version: " + platformVersion);
            environmentNode.log(Status.INFO, "Udid: " + getUdid());

            String appActivity = (String) capabilities.getCapability("appium:appActivity");
            if (!FrontEndOperation.isNullOrEmpty(appActivity)) {
                environmentNode.log(Status.INFO, "App Activity: " + appActivity);
            }

            String appIdentifier = getAppIdentifier();
            if (!FrontEndOperation.isNullOrEmpty(appIdentifier)) {
                environmentNode.log(Status.INFO, "App Identifier: " + appIdentifier);
            }

            if (!FrontEndOperation.isNullOrEmpty(getApp())) {
                environmentNode.log(Status.INFO, "App: " + getApp());
            }
        } else {
            environmentNode.log(Status.INFO, "Browser: " + capabilities.getCapability("browserName"));
            environmentNode.log(Status.INFO, "URL: " + driver.getCurrentUrl());
            environmentNode.log(Status.INFO, "Resolution: " + driver.manage().window().getSize().toString());
            environmentNode.log(Status.INFO, "Accessibility: " + LocalEnviroment.getAccessibility());
            environmentNode.log(Status.INFO, "Operating System: " + platformName);
        }
        mainTest.log(Status.INFO, checks.toString());
        checks.setLength(0); // Clear checks after appending
    }

    // Ends the test and flushes the report
    public static void endTest() {
        extent.flush();
    }
}
