package org.dorijan.rba.tests.web;

import org.dorijan.rba.utilities.Timeouts;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.openqa.selenium.devtools.DevTools;
import org.testng.annotations.BeforeMethod;


public abstract class WebBaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    public DevTools devTools;

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();

        // When running in CI (GitHub Actions sets CI=true), use headless
        if (System.getenv("CI") != null) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
        }

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Timeouts.MEDIUM);
        devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}