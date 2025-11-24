package org.dorijan.rba.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class NumbersGenerator {

    private NumbersGenerator() {
    }

    public static BigDecimal randomAmount(BigDecimal minInclusive, BigDecimal maxInclusive) {
        double min = minInclusive.doubleValue();
        double max = maxInclusive.doubleValue();

        double raw = ThreadLocalRandom.current().nextDouble(min, max);
        BigDecimal value = BigDecimal.valueOf(raw);

        // 2 decimal places, like money
        return value.setScale(2, RoundingMode.HALF_UP);
    }

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
