package org.dorijan.rba.tests.mobile;

import org.dorijan.rba.utilities.AppiumServer;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public abstract class MobileBaseTest {

    protected WebDriver driver;
    private AppiumServer appiumServer;

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws MalformedURLException {
        appiumServer = new AppiumServer();
        appiumServer.start();
        String platform   = System.getProperty("platform", "android").toLowerCase();
        String serverUrl  = System.getProperty("appiumServerUrl", "http://127.0.0.1:4723");
        String deviceName = System.getProperty("deviceName", defaultDeviceName(platform));

        switch (platform) {
            case "android" -> driver = createAndroidDriver(serverUrl, deviceName);
            case "ios"     -> driver = createIOSDriver(serverUrl, deviceName);
            default -> throw new IllegalArgumentException(
                    "Unsupported platform: " + platform + " (use -Dplatform=android or -Dplatform=ios)");
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    private String defaultDeviceName(String platform) {
        return platform.equals("ios") ? "iPhone Simulator" : "Android Device";
    }

    private AndroidDriver createAndroidDriver(String serverUrl, String deviceName)
            throws MalformedURLException {

        UiAutomator2Options options = new UiAutomator2Options();

        options.setPlatformName("Android");
        options.setAutomationName("UiAutomator2");
        options.setDeviceName(deviceName);

        // Browser = Chrome
        options.setCapability("appium:chromedriverAutodownload", true);
        options.setCapability("browserName", "Chrome");

        String udid = System.getProperty("udid");
        if (udid != null && !udid.isBlank()) {
            options.setUdid(udid);
        }

        String chromeDir = System.getProperty("chromedriverDir");
        if (chromeDir != null && !chromeDir.isBlank()) {
            options.setChromedriverExecutableDir(chromeDir);
        }

        return new AndroidDriver(new URL(serverUrl), options);
    }

    private IOSDriver createIOSDriver(String serverUrl, String deviceName)
            throws MalformedURLException {

        XCUITestOptions options = new XCUITestOptions();

        options.setPlatformName("iOS");
        options.setAutomationName("XCUITest");
        options.setDeviceName(deviceName);

        // Browser = Safari (real browser automation on iOS)
        options.setCapability("browserName", "Safari");

        String platformVersion = System.getProperty("platformVersion");
        if (platformVersion != null && !platformVersion.isBlank()) {
            options.setPlatformVersion(platformVersion);
        }

        String udid = System.getProperty("udid");
        if (udid != null && !udid.isBlank()) {
            options.setUdid(udid);
        }

        return new IOSDriver(new URL(serverUrl), options);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        if (appiumServer != null) {
            appiumServer.stop();
        }
    }
}
