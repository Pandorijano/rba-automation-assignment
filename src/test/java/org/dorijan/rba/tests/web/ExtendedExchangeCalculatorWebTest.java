package org.dorijan.rba.tests.web;

import org.dorijan.rba.pages.HomePage;
import org.dorijan.rba.pages.ExchangeCalculatorPage;
import org.dorijan.rba.utilities.JsonParser;
import org.dorijan.rba.utilities.NumbersGenerator;
import org.dorijan.rba.utilities.Currency;
import org.dorijan.rba.utilities.Timeouts;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Reporter;
import java.lang.reflect.Method;
import java.util.ArrayList;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ExtendedExchangeCalculatorWebTest extends WebBaseTest {

    @BeforeMethod
    public void announce(Method method) {
        Reporter.log("=== EXECUTING TEST: " + method.getName() + " ===\r\n", true);
    }

    private JsonParser rateListener;
    private void info(String message) {
        Reporter.log(message, true);
    }

    @Test
    public void verifyExchangeCalculatorUsingApi() {
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

        rateListener = new JsonParser(driver);

        for (BigDecimal moneyValue : moneyValues) {
            info("Buying GBP scenario for " + moneyValue + " EUR." + "\r\n");
            String previousBodyBuy = rateListener.getCurrentBody();
            calculator.buyCurrency(
                    Currency.EUR,
                    Currency.GBP,
                    moneyValue
            );

            JSONObject jsonBuy =
                    rateListener.waitForNewResponse(Timeouts.MEDIUM, previousBodyBuy);

            double exchangeRate = rateListener.getRealExchangeRate(jsonBuy);
            BigDecimal realExchangeRate = BigDecimal.valueOf(exchangeRate);
            BigDecimal exchangeValue = new BigDecimal(calculator.extractExchangeValue());
            BigDecimal expectedExchangeValue = moneyValue.multiply(realExchangeRate).setScale(2, RoundingMode.HALF_UP);

            info("Full exchange rate text: " + calculator.getFullExchangeRateText());
            info("Full exchange amount text: " + calculator.getFullExchangeAmountText());
            info("Full effective rate text: " + calculator.getFullEffectiveRateText());
            info("Full effective amount text: " + calculator.getFullEffectiveAmountText() + "\r\n");

            Assert.assertEquals(exchangeValue, expectedExchangeValue, "Conversion mismatch in exchange result for amount =" + moneyValue);

            info("Selling USD scenario for " + moneyValue + " EUR." + "\r\n");
            String previousBodySell = rateListener.getCurrentBody();

            calculator.sellCurrency(
                    Currency.USD,
                    Currency.EUR,
                    moneyValue
            );

            JSONObject jsonSell =
                    rateListener.waitForNewResponse(Timeouts.MEDIUM, previousBodySell);

            double effectiveRate = rateListener.getRealEffectiveRate(jsonSell);
            BigDecimal realEffectiveRate = BigDecimal.valueOf(effectiveRate);
            BigDecimal effectiveValue = new BigDecimal(calculator.extractEffectiveValue());
            BigDecimal expectedEffectiveValue = moneyValue.multiply(realEffectiveRate).setScale(2, RoundingMode.HALF_UP);

            info("Full exchange rate text: " + calculator.getFullExchangeRateText());
            info("Full exchange amount text: " + calculator.getFullExchangeAmountText());
            info("Full effective rate text: " + calculator.getFullEffectiveRateText());
            info("Full effective amount text: " + calculator.getFullEffectiveAmountText() + "\r\n");

            Assert.assertEquals(effectiveValue, expectedEffectiveValue, "Conversion mismatch in effective exchange result for amount =" + moneyValue);

        }
    }
}
