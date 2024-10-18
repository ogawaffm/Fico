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

    static final PercentageFormatter percentageFormatter = new PercentageFormatter();

    public static String format(LocalDateTime localDateTime) {
        return localDateTime == null ? "null" : localDateTimeFormatter.format(localDateTime);
    }

    public static String format(Duration duration) {
        return duration == null ? "null" : durationFormatter.format(duration);
    }

    public static String format(Double dbl) {
        return dbl == null ? "null" : numberFormatter.format(dbl);
    }

    public static String format(Long lng) {
        return lng == null ? "null" : numberFormatter.format(lng);
    }

    public static String format(Integer integer) {
        return integer == null ? "null" : numberFormatter.format(integer);
    }

    public static String formatAsPercentage(Double percentage) {
        return percentage == null ? null : percentageFormatter.format(percentage);
    }

}
