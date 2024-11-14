package utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import lombok.Getter;
import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;

import static utilities.Constants.SAUCELABS_SESSION_URL;
import static utilities.DriverConfiguration.SLsession;
import static utilities.DriverConfiguration.getDriver;

public class ExtentReport {

    private static RemoteWebDriver driver = (RemoteWebDriver) getDriver();
    private static  Capabilities capabilities = driver.getCapabilities();
    @Getter
    private static ExtentReports extent;
    public static ExtentTest test;

    public static void addMessage(Status status, String message) {
        switch (status){
            case PASS -> test.pass(message);
            case FAIL -> test.fail(message);
            case INFO -> test.info(message);
            case WARNING -> test.warning(message);
            default -> throw new RuntimeException("Status not found");
        }
    }

    public static void addThrowable(Throwable t) {
        test.fail(t);
    }

    public static ExtentReports setUpReport() {
        ExtentSparkReporter spark = new ExtentSparkReporter("target/report.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);

        extent.setSystemInfo("Accessibility", String.valueOf(LocalEnviroment.getAccessibility()));
        String platformName = capabilities.getCapability("platformName").toString();
        extent.setSystemInfo("Platform", platformName);
        extent.setSystemInfo("Provider", LocalEnviroment.getProvider());

        if(LocalEnviroment.isSaucelabs()){
            addSauceLabsParameters(extent);
        } else {
            switch (platformName.toLowerCase()){
                case "android" -> addAndroidParameters(extent);
                case "ios" -> addIosParameters(extent);
                default -> addWebParameters(extent);
            }
        }
        return extent;
    }

    private static void addSauceLabsParameters(ExtentReports extent){
        extent.setSystemInfo("Sauce Labs Session", "<a href='".concat(SAUCELABS_SESSION_URL.concat(SLsession)).concat("' target='_blank'>").concat(SAUCELABS_SESSION_URL.concat(SLsession)).concat("</a>"));
        if (LocalEnviroment.isMobile()) {
            if (FrontEndOperation.isNullOrEmpty(getApp())) {
                extent.setSystemInfo("App", getApp());
            }
            extent.setSystemInfo("AppIdentifier", getAppIdentifier());
            extent.setSystemInfo("AppVersion", SaucelabsDriverConfiguration.appVersion);
            if(!LocalEnviroment.isVirtualDevice()){
                extent.setSystemInfo("Udid", getUdid());
            }
            extent.setSystemInfo("Device Name", getDeviceName());
            extent.setSystemInfo("Platform Version", (String) driver.getCapabilities().getCapability("appium:platformVersion"));
        } else {
            addWebParameters(extent);
        }
    }

    private static void addWebParameters(ExtentReports extent){
        extent.setSystemInfo("Application", "<a href='".concat(DriverConfiguration.setURL()).concat("' target='_blank'>").concat(DriverConfiguration.setURL()).concat("</a>"));
        extent.setSystemInfo("Browser", capabilities.getCapability("browserName").toString().
                concat(" (").concat(((HasCapabilities) getDriver()).getCapabilities().getBrowserVersion().concat(")")));
        extent.setSystemInfo("Resolution", driver.manage().window().getSize().toString());
    }

    private static void addAndroidParameters(ExtentReports extent){
        extent.setSystemInfo("Udid", getUdid());
        if(FrontEndOperation.isNullOrEmpty(getApp())){
            extent.setSystemInfo("AppActivity", (String) capabilities.getCapability("appium:appActivity"));
            extent.setSystemInfo("AppIdentifier", getAppIdentifier());
        } else {
            extent.setSystemInfo("App", getApp());
        }
    }

    private static void addIosParameters(ExtentReports extent){
        extent.setSystemInfo("Udid", getUdid());
        extent.setSystemInfo("AppIdentifier", getAppIdentifier());
    }

    private static String getUdid() {
        return (String)
                capabilities.getCapability(
                        capabilities.getCapabilityNames().contains("appium:udid")
                                ? "appium:udid"
                                : "appium:deviceUDID");
    }

    private static String getAppIdentifier() {
        String appIdentifier =
                (String)
                        capabilities.getCapability(
                                capabilities.getCapabilityNames().contains("appium:appPackage")
                                        ? "appium:appPackage"
                                        : "appium:bundleId");
        return FrontEndOperation.isNullOrEmpty(appIdentifier)
                ? LocalEnviroment.getAppIdentifier()
                : appIdentifier;
    }

    private static String getApp() {
        String app = (String) capabilities.getCapability("appium:app");
        return FrontEndOperation.isNullOrEmpty(app) ? "" : app.substring(app.lastIndexOf("/") + 1);
    }

    private static String getDeviceName() {
        if (LocalEnviroment.isSaucelabs()) {
            if (LocalEnviroment.isVirtualDevice()) {
                return (String) capabilities.getCapability("appium:deviceName");
            }
            return (String) capabilities.getCapability("appium:testobject_device_name");
        } else {
            return (String) capabilities.getCapability("appium:deviceModel");
        }
    }

    public static void addComparative(String comparativeMessage, boolean success) {
        if (success) {
            test.pass(comparativeMessage);
        } else {
            test.fail(comparativeMessage);
        }
    }

    public static void attachTextFileToReport(File file) {
        test.info("Network Lof File: <a href='".concat(file.getAbsolutePath()).concat("'>Download Log</a>"));
    }

    public static void attachScreenshot(WebDriver driver) {
        if (driver instanceof TakesScreenshot) {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            String base64Screenshot = Base64.encodeBase64String(screenshot);
            test.fail(MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build());
        }
    }
}
