package org.dorijan.rba.pages;

import org.dorijan.rba.utilities.Urls;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import java.math.BigDecimal;

public class ExchangeCalculatorPage extends BasePage {

    private final By exchangeRateTypeDropdown = By.id("kurs");
    private final By moneyAmountInput = By.id("suma1");
    private final By currencyFromDropdown = By.id("val1");
    private final By currencyToDropdown = By.id("val2");
    private final By switchCurrencyButton = By.id("switchCurrency");
    public final By exchangeAmount = By.cssSelector("#toHouseExch font font");
    public final By exchangeRate = By.cssSelector("#rateExch font font");
    public final By exchangeLabel = By.cssSelector("label[for='toHouseExch'] label");
    public final By effectiveAmount = By.cssSelector("#toAcctExch font font");
    public final By effectiveRate = By.cssSelector("#rateAcct font font");
    public final By effectiveLabel = By.cssSelector("label[for='toAcctExch'] label");

    public ExchangeCalculatorPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        goTo(Urls.BASE_URL + "/alati/tecajni-kalkulator");
    }

    public void waitToLoad() {
        waitForVisible(exchangeRateTypeDropdown);
    }

    public void selectBuyRate() {
        new Select(waitForVisible(exchangeRateTypeDropdown)).selectByValue("0");
    }

    public void selectMiddleRate() {
        new Select(waitForVisible(exchangeRateTypeDropdown)).selectByValue("1");
    }

    public void selectSellRate() {
        new Select(waitForVisible(exchangeRateTypeDropdown)).selectByValue("2");
    }

    public void selectFromCurrency(String currencyCode) {
        new Select(waitForVisible(currencyFromDropdown)).selectByValue(currencyCode);
    }

    public void selectToCurrency(String currencyCode) {
        new Select(waitForVisible(currencyToDropdown)).selectByValue(currencyCode);
    }

    /**
     * Inputs the money amount into the exchange calculator and waits for the exchange to be calculated.
     *
     * @param amount numeric value representing money
     */
    public void inputMoneyAmount(BigDecimal amount) {
        String value = amount.toPlainString(); // exactly what you type

        type(moneyAmountInput, value);

        wait.until(d -> {
            try {
                String text = d.findElement(exchangeAmount).getText();
                return text != null && text.contains(value);
            } catch (StaleElementReferenceException e) {
                return false; // retry
            }
        });

        wait.until(d -> {
            try {
                String text = d.findElement(effectiveAmount).getText();
                return text != null && text.contains(value);
            } catch (StaleElementReferenceException e) {
                return false; // retry
            }
        });
    }

    /**
     * Extracts the money amount from the full string pulled from the site using a locator.
     *
     * @param locator locator of the element containing the money amount
     * @return a scraped string representing a numeric value
     */
    private String extractExchangeValue(By locator) {
        String fullText = getText(locator);
        String[] textSplit = fullText.split("=");
        String exchangeValueAndCurrency = textSplit[1].trim();
        return exchangeValueAndCurrency.split(" ")[0].replace(",","");
    }

    /**
     * Extracts exchange results and labels from separate elements and combines them into a single string.
     *
     * @param labelTextLocator  locator of the element containing the label text
     * @param amountTextLocator locator of the element containing the money amount text
     * @return a single string with exchange result and label combined
     */
    private String getLabelAndAmountText(By labelTextLocator, By amountTextLocator) {
        String scrapedLabelText = getText(labelTextLocator);
        String scrapedAmountText = getText(amountTextLocator);
        return scrapedLabelText + " " + scrapedAmountText;
    }

    public String getFullExchangeAmountText() {
        return getLabelAndAmountText(
                exchangeLabel,
                exchangeAmount
        );
    }

    public String getFullExchangeRateText() {
        return  getText(exchangeRate);
    }

    public String getFullEffectiveAmountText() {
        return getLabelAndAmountText(
                effectiveLabel,
                effectiveAmount
        );
    }

    public String getFullEffectiveRateText() {
        return getText(effectiveRate);
    }

    public String extractExchangeValue() {
        return extractExchangeValue(exchangeAmount);
    }

    public String extractExchangeRate() {
        return extractExchangeValue(exchangeRate);
    }

    public String extractEffectiveValue() {
        return extractExchangeValue(effectiveAmount);
    }

    public String extractEffectiveRate() {
        return extractExchangeValue(effectiveRate);
    }

    /**
     * Selects selling exchange rate type, selects currencies and inputs money amount.
     *
     * @param currencyFrom currency that is sold
     * @param currencyTo   currency that is bought
     * @param moneyAmount  amount of money sold
     */
    public void sellCurrency(String currencyFrom, String currencyTo, BigDecimal moneyAmount) {
        this.selectSellRate();
        this.selectFromCurrency(currencyFrom);
        this.selectToCurrency(currencyTo);
        this.inputMoneyAmount(moneyAmount);
    }

    /**
     * Selects buying exchange rate type, selects currencies and inputs money amount.
     *
     * @param currencyFrom currency that is used to buy
     * @param currencyTo   currency that is bought
     * @param moneyAmount  amount of money used to buy
     */
    public void buyCurrency(String currencyFrom, String currencyTo, BigDecimal moneyAmount) {
        this.selectBuyRate();
        this.selectFromCurrency(currencyFrom);
        this.selectToCurrency(currencyTo);
        this.inputMoneyAmount(moneyAmount);
    }

}