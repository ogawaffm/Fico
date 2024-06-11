package performance.measuring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ogawa.fico.performance.measuring.StopWatch;
import org.junit.jupiter.api.Test;

public class StopWatchStateTransitionTests {

    /* *********************************************************************************************************** */
    /* ************************************ Tests of illegal state transition ************************************ */
    /* *********************************************************************************************************** */

    @Test
    void testIllegalTransUnstartedToStopped() {
        try {
            StopWatch.create().stop();
        } catch (IllegalStateException e) {
            assertEquals("Cannot stop, stop watch is unstarted", e.getMessage());
        }
    }

    @Test
    void testIllegalTransUnstartedToRecordingByResume() {
        try {
            StopWatch.create().resume();
        } catch (IllegalStateException e) {
            assertEquals("Cannot resume, stop watch is unstarted", e.getMessage());
        }
    }

    @Test
    void testIllegalTransRecordingToRecordingByStart() {
        StopWatch stopWatch = StopWatch.create();
        try {
            stopWatch.start();
            stopWatch.start();
        } catch (IllegalStateException e) {
            assertEquals("Cannot start, stop watch is recording", e.getMessage());
        }
    }

    @Test
    void testIllegalTransRecordingToRecordingByResume() {
        StopWatch stopWatch = StopWatch.create();
        try {
            stopWatch.start();
            stopWatch.stop();
            stopWatch.stop();
        } catch (IllegalStateException e) {
            assertEquals("Cannot stop, stop watch is stopped", e.getMessage());
        }
    }

    @Test
    void testIllegalTransPausedToPaused() {
        StopWatch stopWatch = StopWatch.create();
        try {
            stopWatch.start();
            stopWatch.pause();
            stopWatch.pause();
        } catch (IllegalStateException e) {
            assertEquals("Cannot pause, stop watch is paused", e.getMessage());
        }
    }

    @Test
    void testIllegalTransStoppedToStopped() {
        StopWatch stopWatch = StopWatch.create();
        try {
            stopWatch.start();
            stopWatch.stop();
            stopWatch.stop();
        } catch (IllegalStateException e) {
            assertEquals("Cannot stop, stop watch is stopped", e.getMessage());
        }
    }

    @Test
    void testIllegalTransStoppedToRecordingByStart() {
        StopWatch stopWatch = StopWatch.create();
        try {
            stopWatch.start();
            stopWatch.stop();
            stopWatch.start();
        } catch (IllegalStateException e) {
            assertEquals("Cannot start, stop watch is stopped", e.getMessage());
        }
    }

    @Test
    void testIllegalTransStoppedToRecordingByResume() {
        StopWatch stopWatch = StopWatch.create();
        try {
            stopWatch.start();
            stopWatch.stop();
            stopWatch.resume();
        } catch (IllegalStateException e) {
            assertEquals("Cannot resume, stop watch is stopped", e.getMessage());
        }
    }

    @Test
    void testIllegalTransStoppedToPausedByPause() {
        StopWatch stopWatch = StopWatch.create();
        try {
            stopWatch.start();
            stopWatch.stop();
            stopWatch.pause();
        } catch (IllegalStateException e) {
            assertEquals("Cannot pause, stop watch is stopped", e.getMessage());
        }
    }

    /* *********************************************************************************************************** */
    /* ************************************* Tests of legal state transition ************************************* */
    /* *********************************************************************************************************** */

    @Test
    void testLegalTransitions() {
        StopWatch stopWatch;
        try {
            stopWatch = StopWatch.create();
            stopWatch.start();
            stopWatch.stop();

            stopWatch = StopWatch.create();
            stopWatch.start();
            stopWatch.pause();
            stopWatch.resume();
            stopWatch.pause();
            stopWatch.resume();
            stopWatch.stop();

            stopWatch = StopWatch.create();
            stopWatch.pause();
            stopWatch.resume();
            stopWatch.pause();
            stopWatch.resume();
            stopWatch.stop();

        } catch (Exception e) {
            assertEquals("we do not expect any exceptions here", e.getMessage());
        }
    }

}
