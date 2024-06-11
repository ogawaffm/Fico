package performance.measuring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ogawa.fico.performance.measuring.StopWatch;
import com.ogawa.fico.performance.measuring.StopWatchStatus.State;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import utility.TestUtils;

public class StopWatchMeasuringTests {

    @Test
    void testInternalMeasuring() {

        StopWatch stopWatch;

        // *************************** start/stop using LocalDateTime with nano precision ***************************

        LocalDateTime startLocalDateTime, pauseLocalDateTime, resumeLocalDateTime, stopLocalDateTime, beforeAction;

        stopWatch = StopWatch.create();

        beforeAction = LocalDateTime.now();
        stopWatch.start();
        startLocalDateTime = stopWatch.getStartTimeLocalDateTime();

        // check beforeAction <= startLocalDateTime <= beforeAction + 100 ms
        assertInInterval(beforeAction, MAX_MS_READ_DELAY, startLocalDateTime);

        // record a little bit time
        TestUtils.waitMillis(LITTLE_BIT_TIME_MS);

        beforeAction = LocalDateTime.now();
        stopWatch.pause();
        pauseLocalDateTime = stopWatch.getPauseTimeLocalDateTime();

        // check beforeAction <= pauseLocalDateTime <= beforeAction + MAX_MS_READ_DELAY ms
        assertInInterval(beforeAction, MAX_MS_READ_DELAY, pauseLocalDateTime);

        // pause a little bit time
        TestUtils.waitMillis(LITTLE_BIT_TIME_MS);

        beforeAction = LocalDateTime.now();
        stopWatch.resume();
        resumeLocalDateTime = stopWatch.getRecordingStartTimeLocalDateTime();

        // check beforeAction <= resumeLocalDateTime <= beforeAction + MAX_MS_READ_DELAY ms
        assertInInterval(beforeAction, MAX_MS_READ_DELAY, resumeLocalDateTime);

        // stop logAfter a little bit time
        TestUtils.waitMillis(LITTLE_BIT_TIME_MS);

        beforeAction = LocalDateTime.now();
        stopWatch.stop();
        stopLocalDateTime = stopWatch.getStopTimeLocalDateTime();

        // check beforeAction <= stopLocalDateTime <= beforeAction + 10 * MAX_MS_READ_DELAY ms
        assertInInterval(beforeAction, 10 * MAX_MS_READ_DELAY, stopLocalDateTime);

        checkTimes(
            stopWatch,
            startLocalDateTime,
            stopLocalDateTime,
            pauseLocalDateTime,
            resumeLocalDateTime,
            false
        );

        checkTimes(
            stopWatch,
            toLocalDateTime(stopWatch.getStartTimeInstant()),
            toLocalDateTime(stopWatch.getStopTimeInstant()),
            toLocalDateTime(stopWatch.getPauseTimeInstant()),
            toLocalDateTime(stopWatch.getRecordingStartTimeInstant()),
            false
        );

        checkTimes(
            stopWatch,
            toLocalDateTime(stopWatch.getStartTimeDate()),
            toLocalDateTime(stopWatch.getStopTimeDate()),
            toLocalDateTime(stopWatch.getPauseTimeDate()),
            toLocalDateTime(stopWatch.getRecordingStartTimeDate()),
            true
        );

    }

    @Test
    void testExternalMeasuring() {

        StopWatch stopWatch;

        // ***************************************** start/stop using Date *****************************************

        Date startDate, pauseDate, resumeDate, stopDate;
        Instant startInstant, pauseInstant, resumeInstant, stopInstant;
        LocalDateTime startLocalDateTime, pauseLocalDateTime, resumeLocalDateTime, stopLocalDateTime;

        startLocalDateTime = LocalDateTime.now();
        startInstant = toInstant(startLocalDateTime);
        startDate = Date.from(startInstant);

        pauseLocalDateTime = startLocalDateTime.plusSeconds(1);
        pauseInstant = toInstant(pauseLocalDateTime);
        pauseDate = Date.from(pauseInstant);

        resumeLocalDateTime = pauseLocalDateTime.plusSeconds(1);
        resumeInstant = toInstant(resumeLocalDateTime);
        resumeDate = Date.from(resumeInstant);

        stopLocalDateTime = resumeLocalDateTime.plusSeconds(1);
        stopInstant = toInstant(stopLocalDateTime);
        stopDate = Date.from(stopInstant);

        // *********************************** start/pause/resume/stop using Date ************************************

        stopWatch = StopWatch.create();

        stopWatch.start(startDate);
        stopWatch.pause(pauseDate);
        stopWatch.resume(resumeDate);
        stopWatch.stop(stopDate);

        checkTimes(
            stopWatch,
            toLocalDateTime(startDate),
            toLocalDateTime(stopDate),
            toLocalDateTime(pauseDate),
            toLocalDateTime(resumeDate),
            true
        );

        // ********************************* start/pause/resume/stop using Instant **********************************

        stopWatch = StopWatch.create();

        stopWatch.start(startInstant);
        stopWatch.pause(pauseInstant);
        stopWatch.resume(resumeInstant);
        stopWatch.stop(stopInstant);

        checkTimes(
            stopWatch,
            toLocalDateTime(startInstant),
            toLocalDateTime(stopInstant),
            toLocalDateTime(pauseInstant),
            toLocalDateTime(resumeInstant),
            false
        );

        // ****************************** start/pause/resume/stop using LocalDateTime *******************************

        stopWatch = StopWatch.create();

        stopWatch.start(startLocalDateTime);
        stopWatch.pause(pauseLocalDateTime);
        stopWatch.resume(resumeLocalDateTime);
        stopWatch.stop(stopLocalDateTime);

        checkTimes(
            stopWatch,
            startLocalDateTime,
            stopLocalDateTime,
            pauseLocalDateTime,
            resumeLocalDateTime,
            false
        );

    }

    /* *********************************************************************************************************** */
    /* ************************************************ Time Check *********************************************** */
    /* *********************************************************************************************************** */

    /**
     * I1 little bit time in milliseconds to wait for the stop watch to measure.
     */
    private static final int LITTLE_BIT_TIME_MS = 100;

    /**
     * Maximum delay in milliseconds until the measured time is read from the stop watch.
     */
    private static final long MAX_MS_READ_DELAY = 100;

    /**
     * Maximum error in milliseconds when comparing nanosecond precision with millisecond precision, measured in ns.
     */
    private static final long MAX_NS_TO_MS_ERROR = 999_999L;

    /**
     * Check all times of the stop watch against the given expected dates.
     *
     * @param stopWatch              Stop watch to check
     * @param expStartLocalDateTime  Expected start date
     * @param expStopLocalDateTime   Expected stop date
     * @param expPauseLocalDateTime  Expected pause date
     * @param expResumeLocalDateTime Expected resume date
     */
    void checkTimes(
        @NonNull StopWatch stopWatch,
        LocalDateTime expStartLocalDateTime,
        LocalDateTime expStopLocalDateTime,
        LocalDateTime expPauseLocalDateTime,
        LocalDateTime expResumeLocalDateTime,
        boolean expValuesHaveMilliPrecision) {

        // Definition of all actual times variants (LocalDateTime, Instant, Date) for all states
        LocalDateTime actStartLocalDateTime, actPauseLocalDateTime, actResumeLocalDateTime, actStopLocalDateTime;
        Instant actStartInstant, actPauseInstant, actResumeInstant, actStopInstant;
        Date actStartDate, actPauseDate, actResumeDate, actStopDate;

        // read all actual times from the stop watch in date variants
        actStartDate = stopWatch.getStartTimeDate();
        actPauseDate = stopWatch.getPauseTimeDate();
        actResumeDate = stopWatch.getRecordingStartTimeDate();
        actStopDate = stopWatch.getStopTimeDate();

        // Have to cast to date precision because the passed dates are only precise to milliseconds?
        if (expValuesHaveMilliPrecision) {

            // assign LocalDateTime variants using a cast to date precision
            actStartLocalDateTime = toLocalDateTime(toDate(stopWatch.getStartTimeLocalDateTime()));
            actPauseLocalDateTime = toLocalDateTime(toDate(stopWatch.getPauseTimeLocalDateTime()));
            actResumeLocalDateTime = toLocalDateTime(toDate(stopWatch.getRecordingStartTimeLocalDateTime()));
            actStopLocalDateTime = toLocalDateTime(toDate(stopWatch.getStopTimeLocalDateTime()));

            // assign Instant variants using a cast to date precision
            actStartInstant = toInstant(toDate(stopWatch.getStartTimeInstant()));
            actPauseInstant = toInstant(toDate(stopWatch.getPauseTimeInstant()));
            actResumeInstant = toInstant(toDate(stopWatch.getRecordingStartTimeInstant()));
            actStopInstant = toInstant(toDate(stopWatch.getStopTimeInstant()));

        } else {

            // no, just assign

            // assign LocalDateTime variants
            actStartLocalDateTime = stopWatch.getStartTimeLocalDateTime();
            actPauseLocalDateTime = stopWatch.getPauseTimeLocalDateTime();
            actResumeLocalDateTime = stopWatch.getRecordingStartTimeLocalDateTime();
            actStopLocalDateTime = stopWatch.getStopTimeLocalDateTime();

            // assign Instant variants
            actStartInstant = stopWatch.getStartTimeInstant();
            actPauseInstant = stopWatch.getPauseTimeInstant();
            actResumeInstant = stopWatch.getRecordingStartTimeInstant();
            actStopInstant = stopWatch.getStopTimeInstant();

        }

        if (expStartLocalDateTime == null) {
            assertEquals(null, actStartDate);
            assertEquals(null, actStartLocalDateTime);
            assertEquals(null, actStartInstant);

            assertEquals(null, actPauseDate);
            assertEquals(null, actPauseLocalDateTime);
            assertEquals(null, actPauseInstant);

            assertEquals(null, actResumeDate);
            assertEquals(null, actResumeLocalDateTime);
            assertEquals(null, actResumeInstant);

            assertEquals(null, actStopDate);
            assertEquals(null, actStopLocalDateTime);
            assertEquals(null, actStopInstant);

            assertEquals(null, stopWatch.getTotalTime());
            assertEquals(null, stopWatch.getAccumulatedRecordedTime());

            assertEquals(State.UNSTARTED, stopWatch.getState());

            return;

        }

        // Check start times
        assertEquals(toDate(expStartLocalDateTime), actStartDate);
        assertEquals(expStartLocalDateTime, actStartLocalDateTime);
        assertEquals(toInstant(expStartLocalDateTime), actStartInstant);

        if (expStopLocalDateTime != null) {
            // Check stop times
            assertEquals(toDate(expStopLocalDateTime), actStopDate);
            assertEquals(expStopLocalDateTime, actStopLocalDateTime);
            assertEquals(toInstant(expStopLocalDateTime), actStopInstant);

            // stop equal or logAfter start - no need to check for group variant because start/stop variants are equal
            assertEqualOrAfter(expStartLocalDateTime, expStopLocalDateTime);
        } else {
            assertEquals(null, actStopDate);
            assertEquals(null, actStopLocalDateTime);
            assertEquals(null, actStopInstant);
        }

        // Check pause times
        if (expPauseLocalDateTime != null) {
            assertEquals(toDate(expPauseLocalDateTime), actPauseDate);
            assertEquals(toInstant(expPauseLocalDateTime), actPauseInstant);
            assertEquals(expPauseLocalDateTime, actPauseLocalDateTime);
        } else {
            assertEquals(null, actPauseDate);
            assertEquals(null, actPauseLocalDateTime);
            assertEquals(null, actPauseInstant);
        }

        // Check resume times
        if (expResumeLocalDateTime != null) {
            assertEquals(toDate(expResumeLocalDateTime), actResumeDate);
            assertEquals(expResumeLocalDateTime, actResumeLocalDateTime);
            assertEquals(toInstant(expResumeLocalDateTime), actResumeInstant);

            // no resume without pause
            assertTrue(expPauseLocalDateTime != null);
            // resume equal or logAfter pause
            assertEqualOrAfter(expPauseLocalDateTime, expResumeLocalDateTime);

        } else {
            assertEquals(null, actResumeDate);
            assertEquals(null, actResumeLocalDateTime);
            assertEquals(null, actResumeInstant);
        }

        // Check total times
        Duration expTotalDuration = Duration.between(expStartLocalDateTime, expStopLocalDateTime);

        if (expValuesHaveMilliPrecision) {
            // since start and stop in StopWatch are measured in nanoseconds,
            // there could be a rounding error of a maximum of 2 * 999 milliseconds
            assertEqualOrNsLonger(expTotalDuration, MAX_NS_TO_MS_ERROR, stopWatch.getTotalTime());
        } else {
            assertEquals(expTotalDuration, stopWatch.getTotalTime());
        }

        // Check measured times
        Duration expMeasuredDuration;

        // if there is a pause
        if (expPauseLocalDateTime != null) {

            // if there is a resume
            if (expResumeLocalDateTime != null) {

                // yes, there is a whole pause/resume cycle
                Duration pausedDuration = Duration.between(expPauseLocalDateTime, expResumeLocalDateTime);

                // stopped?
                if (expStopLocalDateTime != null) {

                    expTotalDuration = Duration.between(expStartLocalDateTime, expStopLocalDateTime);
                    expMeasuredDuration = expTotalDuration.minus(pausedDuration);

                    assertEqualOrNsLonger(
                        expTotalDuration,
                        expValuesHaveMilliPrecision ? MAX_NS_TO_MS_ERROR : 0,
                        stopWatch.getTotalTime()
                    );

                    assertEqualOrNsLonger(
                        expMeasuredDuration,
                        expValuesHaveMilliPrecision ? MAX_NS_TO_MS_ERROR : 0,
                        stopWatch.getAccumulatedRecordedTime()
                    );

                } else {
                    // no, still recording
                    expMeasuredDuration =
                        Duration.between(expStartLocalDateTime, LocalDateTime.now()).minus(pausedDuration);

                    assertEqualOrMsLonger(expMeasuredDuration, MAX_MS_READ_DELAY,
                        stopWatch.getAccumulatedRecordedTime());
                    assertTrue(expMeasuredDuration.compareTo(stopWatch.getAccumulatedRecordedTime()) <= 0);

                    expTotalDuration = Duration.between(expStartLocalDateTime, LocalDateTime.now());

                    assertEqualOrMsLonger(expTotalDuration, MAX_MS_READ_DELAY, stopWatch.getTotalTime());
                    assertTrue(expTotalDuration.compareTo(stopWatch.getTotalTime()) <= 0);

                }

            } else {

                // no, there is only a pause
                assertEquals(State.PAUSED, stopWatch.getState());
                expMeasuredDuration = Duration.between(expStartLocalDateTime, expPauseLocalDateTime);
                assertEquals(expMeasuredDuration, stopWatch.getAccumulatedRecordedTime());
                assertEquals(expMeasuredDuration, stopWatch.getTotalTime());

            }
        } else {
            // there was no pause!
            // stopped?
            if (expStopLocalDateTime != null) {
                expTotalDuration = Duration.between(expStartLocalDateTime, expStopLocalDateTime);
                assertEquals(expTotalDuration, stopWatch.getAccumulatedRecordedTime());
                assertEquals(expTotalDuration, stopWatch.getTotalTime());
            } else {
                // no, still recording
                expMeasuredDuration = Duration.between(expStartLocalDateTime, LocalDateTime.now());

                assertEqualOrMsLonger(expMeasuredDuration, MAX_MS_READ_DELAY, stopWatch.getAccumulatedRecordedTime());
                assertTrue(expMeasuredDuration.compareTo(stopWatch.getAccumulatedRecordedTime()) <= 0);

                expTotalDuration = Duration.between(expStartLocalDateTime, Instant.now());

                assertEqualOrMsLonger(expTotalDuration, MAX_MS_READ_DELAY, stopWatch.getTotalTime());
                assertTrue(expTotalDuration.compareTo(stopWatch.getTotalTime()) <= 0);
            }
        }
    }

    /* *********************************************************************************************************** */
    /* ************************************************** Casting ************************************************ */
    /* *********************************************************************************************************** */
    private static Date toDate(Instant instant) {
        return instant == null ? null : Date.from(instant);
    }

    private static Date toDate(LocalDateTime localDateTime) {
        return localDateTime == null ? null : Date.from(toInstant(localDateTime));
    }

    private static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    private static Instant toInstant(Date date) {
        return date == null ? null : date.toInstant();
    }

    private static LocalDateTime toLocalDateTime(Date date) {
        return date == null ? null : LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private static LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /* *********************************************************************************************************** */
    /* ************************************************ Assertions *********************************************** */
    /* *********************************************************************************************************** */

    void assertInInterval(LocalDateTime first, long maxMillisAfter, LocalDateTime actual) {
        assertNotNull(first, "first must not be null");
        assertNotNull(actual, "actual must not be null");
        LocalDateTime second = first.plus(Duration.ofMillis(maxMillisAfter));
        assertTrue(!second.isBefore(actual),
            "first=" + first + ", maxMillisAfter=" + maxMillisAfter + ", actual=" + actual);
    }

    void assertEqualOrAfter(LocalDateTime first, LocalDateTime second) {
        assertNotNull(first, "first must not be null");
        assertNotNull(second, "second must not be null");
        assertTrue(!second.isBefore(first), "first=" + first + ", second=" + second);
    }

    void assertEqualOrMsLonger(Duration expected, long maxMillisAfter, Duration actual) {
        assertNotNull(expected, "expected must not be null");
        assertNotNull(actual, "actual must not be null");
        assertTrue(!expected.plusMillis(maxMillisAfter).minus(actual).isNegative(),
            "expected=" + expected + ", actual=" + actual + ", maxMillisAfter=" + maxMillisAfter);
    }

    void assertEqualOrNsLonger(Duration expected, long maxNanosAfter, Duration actual) {
        assertNotNull(expected, "expected must not be null");
        assertNotNull(actual, "actual must not be null");
        assertTrue(!expected.plusNanos(maxNanosAfter).minus(actual).isNegative(),
            "expected=" + expected + ", actual=" + actual + ", maxNanosAfter=" + maxNanosAfter);
    }

}
