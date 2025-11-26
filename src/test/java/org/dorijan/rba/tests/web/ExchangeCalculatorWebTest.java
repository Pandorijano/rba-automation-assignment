package org.dorijan.rba.tests.web;

import org.dorijan.rba.pages.HomePage;
import org.dorijan.rba.pages.ExchangeCalculatorPage;
import org.dorijan.rba.utilities.NumbersGenerator;
import org.dorijan.rba.utilities.Currency;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Reporter;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.List;
import java.lang.reflect.Method;

public class ExchangeCalculatorWebTest extends WebBaseTest {

    @BeforeMethod
    public void announce(Method method) {
        Reporter.log("=== EXECUTING TEST: " + method.getName() + " ===\r\n", true);
    }

    private void info(String message) {
        Reporter.log(message, true);
    }

    @Test
    public void verifyExchangeCalculator() {
        HomePage home = new HomePage(driver);
        ExchangeCalculatorPage calculator = new ExchangeCalculatorPage(driver);

        List<BigDecimal> moneyValues =
                NumbersGenerator.randomAmountList(
                        5,
                        new BigDecimal("10.00"),
                        new BigDecimal("1000.00")
                );

        info("Opening home page" + "\r\n");
        home.open();

        info("Navigating to the exchange calculator" + "\r\n");
        home.navigateToExchangeCalculator();

        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(1));
        calculator.waitToLoad();

        for (BigDecimal moneyValue : moneyValues) {
            info("Buying GBP scenario for " + moneyValue + " EUR." + "\r\n");
            calculator.buyCurrency(
                    Currency.EUR,
                    Currency.GBP,
                    moneyValue
            );

            info("Full exchange rate text: " + calculator.getFullExchangeRateText());
            info("Full exchange amount text: " + calculator.getFullExchangeAmountText());
            info("Full effective rate text: " + calculator.getFullEffectiveRateText());
            info("Full effective amount text: " + calculator.getFullEffectiveAmountText() + "\r\n");

            info("Selling USD scenario for " + moneyValue + " EUR." + "\r\n");
            calculator.sellCurrency(
                    Currency.USD,
                    Currency.EUR,
                    moneyValue
            );

            info("Full exchange rate text: " + calculator.getFullExchangeRateText());
            info("Full exchange amount text: " + calculator.getFullExchangeAmountText());
            info("Full effective rate text: " + calculator.getFullEffectiveRateText());
            info("Full effective amount text: " + calculator.getFullEffectiveAmountText() + "\r\n");
        }
    }
}
