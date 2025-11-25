package org.dorijan.rba.pages;

import org.dorijan.rba.utilities.Timeouts;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Timeouts.SHORT);
        this.wait.ignoring(StaleElementReferenceException.class);
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void waitForInvisible(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void scrollToElement(By locator) {
        WebElement element = waitForVisible(locator);
        new org.openqa.selenium.interactions.Actions(driver)
                .moveToElement(element)
                .perform();
    }

    protected void click(By locator) {
        wait.until(d -> {
            try {
                WebElement el = d.findElement(locator);
                el.click();
                return true;
            } catch (StaleElementReferenceException e) {
                return false;
            }
        });
    }

    protected void type(By locator, String text) {
        wait.until(d -> {
            try {
                WebElement el = d.findElement(locator);
                el.clear();
                el.sendKeys(text);
                return true;
            } catch (StaleElementReferenceException e) {
                return false; // retry
            }
        });
    }

    protected String getText(By locator) {
        return wait.until(d -> {
            try {
                WebElement el = d.findElement(locator);
                String text = el.getText();
                return text == null ? null : text.trim();
            } catch (StaleElementReferenceException e) {
                return null; // retry
            }
        });
    }

    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    protected void goTo(String url) {
        driver.get(url);
    }

    protected void waitForTextToChange(By locator, String oldValue) {
        wait.until(ExpectedConditions.not(
                ExpectedConditions.textToBe(locator, oldValue)
        ));
    }

}
