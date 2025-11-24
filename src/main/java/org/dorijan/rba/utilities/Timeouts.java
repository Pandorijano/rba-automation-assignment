package org.dorijan.rba.utilities;

import java.time.Duration;

public final class Timeouts {

    private Timeouts() {} // prevents instantiation

    public static final Duration SHORT = Duration.ofSeconds(5);
    public static final Duration MEDIUM = Duration.ofSeconds(10);
    public static final Duration LONG = Duration.ofSeconds(30);
    public static final Duration VERY_LONG = Duration.ofSeconds(60);
}
