package org.dorijan.rba.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class NumbersGenerator {

    private NumbersGenerator() {
    }

    /**
     * Generates a random number between the specified minimum and maximum values, the number is rounded to two decimals.
     *
     * @param minInclusive the minimum allowed value (inclusive)
     * @param maxInclusive the maximum allowed value (inclusive)
     * @return a random {@link BigDecimal} with two decimal places between minInclusive and maxInclusive
     */
    public static BigDecimal randomAmount(BigDecimal minInclusive, BigDecimal maxInclusive) {
        double min = minInclusive.doubleValue();
        double max = maxInclusive.doubleValue();

        double raw = ThreadLocalRandom.current().nextDouble(min, max);
        BigDecimal value = BigDecimal.valueOf(raw);

        return value.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Generates a list of random number based on the {@link #randomAmount(BigDecimal, BigDecimal)} method.
     *
     * @param count        the number of random values to generate
     * @param minInclusive the minimum allowed value (inclusive)
     * @param maxInclusive the maximum allowed value (inclusive)
     * @return a list of {@link BigDecimal} values representing random currency amounts
     */
    public static List<BigDecimal> randomAmountList(
            int count,
            BigDecimal minInclusive,
            BigDecimal maxInclusive
    ) {
        List<BigDecimal> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            result.add(randomAmount(minInclusive, maxInclusive));
        }
        return result;
    }
}
