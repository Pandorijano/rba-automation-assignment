package org.dorijan.rba.pages;

import org.dorijan.rba.utilities.Urls;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {

    private final By acceptCookiesButton = By.id("onetrust-accept-btn-handler");
    private final By cookiesOverlay = By.cssSelector(".onetrust-pc-dark-filter");
    private final By exchangeRateCalculatorButton = By.id("button-3faa825664");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        goTo(Urls.BASE_URL);
        this.waitToLoad();
        this.acceptCookies();
    }

    public void waitToLoad() {
        waitForVisible(cookiesOverlay);
    }

    private void scrollToExchangeCalculatorButton() {
        scrollToElement(exchangeRateCalculatorButton);
    }

    private void clickExchangeCalculatorButton() {
        click(exchangeRateCalculatorButton);
    }

    public void acceptCookies() {
        if (isDisplayed(acceptCookiesButton)) {
            click(acceptCookiesButton);
            waitForInvisible(cookiesOverlay);
        }
    }

    public void navigateToExchangeCalculator() {
        scrollToExchangeCalculatorButton();
        clickExchangeCalculatorButton();
    }
}
