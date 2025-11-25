# RBA Automation Assignment

## Setup

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
## How it works

I've created two tests:
- `src/test/java/org/dorijan/rba/tests/ExchangeCalculatorTest.java`
  - developed according to the assignment, it navigates to the exchange calculator inputs 5 random values \
    and saves the exchange value and rate 
- `src/test/java/org/dorijan/rba/tests/ExtendedExchangeCalculatorTest.java`
  - based on the test from the assignment but expanded with an API check that extracts the full exchange rates which \
    are not rounded to 2 decimal points, it uses those rates to calculate the expected exchange value results and \
    verifies the values that are shown to the user

### Utilities
- `src/main/java/org/dorijan/rba/utilities/JsonParser.java` 
  - API helper that extracts the `exchangeRate` and `effectiveRate` values from the JSON in the response of the API \
    call that is made each time a new value is input in the exchange calculator 
- `src/main/java/org/dorijan/rba/utilities/NumbersGenerator.java`
  - a custom method which creates a list of random numbers rounded up to two decimals used to simulate money amounts 

### Known issues/obstacles
- While populating the input field in the exchange calculator for the first time in the iteration, number 0 is present \
    in front of the value the test inputs, it does not affect the result of the exchange
- The `ExtendedExchangeCalculatorTest` randomly does not manage to fetch JSON form the `calculateExchangeRate` API, \
  as it is not a part of the original assignment, the GHA workflow config only targets the original test
- Due to constant webpage changes and value comparisons certain methods wait until the page is fully built by catching \
  the `StaleElementReferenceException`

## Run tests

#### Run all

```sh
mvn test
```

#### Run single test
```sh
mvn -Dtest=ExchangeCalculatorTest test
```