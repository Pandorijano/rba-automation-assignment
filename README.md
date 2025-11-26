# RBA Automation Assignment

## Setup

Before continuing with the project make sure you have Appium and Appium drivers installed. 

1. Clone the repo

```sh
git clone https://github.com/Pandorijano/rba-automation-assignment.git
```

2. Move to the directory

```sh
cd rba-automation-assignment
```

3. Install packages

```sh
mvn clean install
```

## Project Structure

The project is structured using the page object pattern.

- `src/main/java/org.dorijan.rba/pages` contains pages with locators and custom methods
- `src/main/java/org.dorijan.rba/utilities` contains helper methods and constants
- `src/test/java/org.dorijan.rba.tests` contains tests files
- `postman` contains postman collection and environment for daily postman tests on GHA

## How it works

For Selenium task I've created two tests:
- `src/test/java/org/dorijan/rba/tests/web/ExchangeCalculatorWebTest.java`
  - developed according to the assignment, it navigates to the exchange calculator inputs 5 random values \
    and saves the exchange value and rate 
- `src/test/java/org/dorijan/rba/tests/web/ExtendedExchangeCalculatorWebTest.java`
  - based on the test from the assignment but expanded with an API check that extracts the full exchange rates which \
    are not rounded to 2 decimal points, it uses those rates to calculate the expected exchange value results and \
    verifies the values that are shown to the user 
  
For the Appium task I've created one test:
- `src/test/java/org/dorijan/rba/tests/mobile/ExchangeCalculatorMobileTest.java`
  - the test is the exact same copy of the Selenium task test, as it runs the same test in Chrome on a mobile device, \
    the test can be run locally on an Android device if a Windows pc/laptop is used to host the test, iOS setup is \
    included in the `MobileBaseTest` but I was unable to test it with my setup 

### Utilities
- `src/main/java/org/dorijan/rba/utilities/JsonParser.java` 
  - API helper that extracts the `exchangeRate` and `effectiveRate` values from the JSON in the response of the API \
    call that is made each time a new value is input in the exchange calculator 
- `src/main/java/org/dorijan/rba/utilities/NumbersGenerator.java`
  - a custom method which creates a list of random numbers rounded up to two decimals used to simulate money amounts 
- `src/main/java/org/dorijan/rba/utilities/AppiumServer.java`
  - a helper which starts an Appium server with chromedriver autodownload enabled to avoid the "No Chromedriver found"\
    error, it's set to hide the Appium server output from the terminal 

### Known issues/obstacles
- While populating the input field in the exchange calculator for the first time in the iteration, number 0 is present \
    in front of the value the test inputs, it does not affect the result of the exchange
- The `ExtendedExchangeCalculatorTest` randomly does not manage to fetch JSON form the `calculateExchangeRate` API
- Due to constant webpage changes and value comparisons certain methods wait until the page is fully built by catching \
  the `StaleElementReferenceException`

## Run tests

#### Run all
Run all tests if you have your mobile device connected to the host machine 
```sh
mvn test
```

#### Run single selenium test
```sh
mvn -Dtest=ExchangeCalculatorTest test
```

#### Run appium test
Replace {device udid} with your actual mobile device udid (found by running `adb devices`)
```sh
mvn test -Dtest=ExchangeCalculatorMobileTest -Dplatform=android -Dudid={device udid}
```