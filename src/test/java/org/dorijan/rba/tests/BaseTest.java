package org.dorijan.rba.tests;

import org.dorijan.rba.utilities.Timeouts;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.openqa.selenium.devtools.DevTools;
import org.testng.annotations.BeforeMethod;


public abstract class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    public DevTools devTools;

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
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