package com.ogawa.fico.performance.logging;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Formatter for durations
 */
public class DurationFormatter {

    /**
     * Formatter for time values (HH:mm:ss)
     */
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME;

    /**
     * Decimal separator of the current locale
     */
    final private char decimalSeparator;

    /**
     * Formatter for number of days
     */
    final private DecimalFormat daysFormatter;

    /**
     * Creates a new DurationFormatter using the default locale
     */
    public DurationFormatter() {
        this(Locale.getDefault(Locale.Category.FORMAT));
    }

    /**
     * Creates a new DurationFormatter using the given locale
     *
     * @param locale Locale
     */
    public DurationFormatter(Locale locale) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
        decimalSeparator = symbols.getDecimalSeparator();
        daysFormatter = new DecimalFormat("#,##0", symbols);
    }

    /**
     * Formats the given duration
     *
     * @param duration Duration
     * @return formatted duration
     */
    public String format(Duration duration) {
        StringBuilder sb = new StringBuilder();
        append(sb, duration);
        return sb.toString();
    }

    /**
     * Appends the given duration to the given StringBuilder
     *
     * @param sb       StringBuilder
     * @param duration Duration
     */
    public void append(StringBuilder sb, Duration duration) {

        Duration remainingDuration = duration;

        long days = duration.toDays();
        remainingDuration = remainingDuration.minusDays(days);

        long hours = remainingDuration.toHours();
        remainingDuration = remainingDuration.minusHours(hours);

        long minutes = remainingDuration.toMinutes();
        remainingDuration = remainingDuration.minusMinutes(minutes);

        long seconds = remainingDuration.getSeconds();
        remainingDuration = remainingDuration.minusSeconds(seconds);

        if (days > 0) {
            if (days == 1L) {
                sb.append("1 day ");
            } else if (days >= 1000L) {
                sb.append(daysFormatter.format(days)).append(" days ");
            } else {
                sb.append(days).append(" days ");
            }
        }

        // Is the duration at least one minute?
        if (days + hours + minutes > 0L) {
            // yes, so format the time part as HH:mm:ss
            // there is no need to show the seconds fraction (nanoseconds)
            LocalTime time = LocalTime.of((int) hours, (int) minutes, (int) seconds);
            sb.append(timeFormatter.format(time));
        } else {

            // no, the duration is second or second fraction (nanoseconds) only

            sb.append(seconds);

            long nanos = remainingDuration.getNano();

            sb.append(decimalSeparator);
            // plus nanoseconds as full size (9 digits) without trailing zeros

            String ns = Long.toString(nanos);

            if (nanos == 0) {
                sb.append("0");
            } else {

                // append leading zeros
                sb.append("00000000".substring(ns.length() - 1));

                // determine last non-zero index (to avoid trailing zeros to be appended)
                int lastNonZeroIndex = ns.length() - 1;

                for (int i = ns.length() - 1; i >= 0; i--) {
                    if (ns.charAt(i) != '0') {
                        lastNonZeroIndex = i;
                        break;
                    }
                }
                // append mantissa up to last non-zero index
                sb.append(ns.substring(0, lastNonZeroIndex + 1));
            }

            sb.append(" seconds");

        }

    }

}
