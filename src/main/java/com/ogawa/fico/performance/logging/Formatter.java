package com.ogawa.fico.performance.logging;

import java.time.LocalDateTime;
import java.time.Duration;

/**
 * application's standard Formatter for various types of values
 */
public class Formatter {

    static final LocalDateTimeFormatter localDateTimeFormatter = new LocalDateTimeFormatter();

    static final DurationFormatter durationFormatter = new DurationFormatter();

    static final NumberFormatter numberFormatter = new NumberFormatter();

    public static String format(LocalDateTime localDateTime) {
        return localDateTimeFormatter.format(localDateTime);
    }

    public static String format(Duration duration) {
        return durationFormatter.format(duration);
    }

    public static String format(Double dbl) {
        return numberFormatter.format(dbl);
    }

    public static String format(Long lng) {
        return numberFormatter.format(lng);
    }

    public static String format(Integer integer) {
        return numberFormatter.format(integer);
    }

}
