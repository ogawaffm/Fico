package performance.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ogawa.fico.performance.logging.DurationFormatter;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

public class DurationFormatterTests {

    private final DurationFormatter durationFormatter = new DurationFormatter();

    // short-cut for durationFormatter.format(duration)
    private String format(Duration duration) {
        return durationFormatter.format(duration);
    }

    void testNanos(int sec) {

        ChronoUnit ns = ChronoUnit.NANOS;
        Duration s = Duration.of(sec, ChronoUnit.SECONDS);

        // test ns with without trailing zeros and with leading zeros

        assertEquals(sec + ".0 seconds", format(Duration.ZERO.plus(s)));
        assertEquals(sec + ".000000001 seconds", format(Duration.of(1, ns).plus(s)));
        assertEquals(sec + ".00000001 seconds", format(Duration.of(10, ns).plus(s)));
        assertEquals(sec + ".01 seconds", format(Duration.of(10000000, ns).plus(s)));
        assertEquals(sec + ".1 seconds", format(Duration.of(100000000, ns).plus(s)));
        assertEquals(sec + ".123456789 seconds", format(Duration.of(123456789, ns).plus(s)));
        assertEquals(sec + ".100000009 seconds", format(Duration.of(100000009, ns).plus(s)));

    }

    @Test
    void testNanosWith0Sec() {
        testNanos(0);
    }

    @Test
    void testNanosWith1Sec() {
        testNanos(1);
    }

    @Test
    void testNanosWith10Sec() {
        testNanos(10);
    }

    @Test
    void testNanosWith12Sec() {
        testNanos(12);
    }

    @Test
    void testWithMinutes() {

        ChronoUnit min = ChronoUnit.MINUTES;
        ChronoUnit sec = ChronoUnit.SECONDS;

        assertEquals("00:01:00", format(Duration.of(1, min)));

        assertEquals("00:01:01", format(Duration.of(1, min).plus(Duration.of(1, sec))));

        assertEquals("00:01:59", format(Duration.of(1, min).plus(Duration.of(59, sec))));

        assertEquals("00:12:34", format(Duration.of(12, min).plus(Duration.of(34, sec))));

        assertEquals("00:59:59", format(Duration.of(59, min).plus(Duration.of(59, sec))));

        assertEquals("01:00:59", format(Duration.of(60, min).plus(Duration.of(59, sec))));

        assertEquals("23:46:59", format(Duration.of(23 * 60 + 46, min).plus(Duration.of(59, sec))));

    }

    @Test
    void testWithDays() {
        ChronoUnit day = ChronoUnit.DAYS;
        Duration dur_12_34_56 = Duration.of(12, ChronoUnit.HOURS)
            .plus(Duration.of(34, ChronoUnit.MINUTES))
            .plus(Duration.of(56, ChronoUnit.SECONDS));

        // check the omitting of "day(s)"
        assertEquals("12:34:56", format(Duration.of(0, day).plus(dur_12_34_56)));

        // check the invoke of "day" for one day
        assertEquals("1 day 00:00:00", format(Duration.of(1, day)));

        // check the correct time formatting for one day
        assertEquals("1 day 12:34:56", format(Duration.of(1, day).plus(dur_12_34_56)));

        // check the invoke of "days" for > one day
        assertEquals("2 days 00:00:00", format(Duration.of(2, day)));

        // check the correct time formatting for > one day
        assertEquals("2 days 12:34:56", format(Duration.of(2, day).plus(dur_12_34_56)));

        // check the non-grouping of days
        assertEquals("6789 days 12:34:56", format(Duration.of(6789, day).plus(dur_12_34_56)));

    }
}
