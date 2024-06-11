package performance.measuring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ogawa.fico.performance.measuring.StopWatch;
import com.ogawa.fico.performance.measuring.StopWatchStatus.State;
import org.junit.jupiter.api.Test;

public class StopWatchStateTest {

    @Test
    void testUnstartedState() {
        StopWatch stopWatch = StopWatch.create();

        assertEquals(false, stopWatch.isRecording());
        assertEquals(false, stopWatch.isPaused());
        assertEquals(false, stopWatch.isStopped());

        assertEquals(true, stopWatch.isStartable());
        assertEquals(true, stopWatch.isPausable());
        assertEquals(false, stopWatch.isResumable());
        assertEquals(false, stopWatch.isStoppable());

        assertEquals(State.UNSTARTED, stopWatch.getState());

    }

    @Test
    void testRecordingState() {
        StopWatch stopWatch = StopWatch.create();
        stopWatch.start();

        assertEquals(true, stopWatch.isRecording());
        assertEquals(false, stopWatch.isPaused());
        assertEquals(false, stopWatch.isStopped());

        assertEquals(false, stopWatch.isStartable());
        assertEquals(true, stopWatch.isPausable());
        assertEquals(false, stopWatch.isResumable());
        assertEquals(true, stopWatch.isStoppable());

        assertEquals(State.RECORDING, stopWatch.getState());

    }

    @Test
    void testPausedState() {
        StopWatch stopWatch = StopWatch.create();
        stopWatch.pause();

        assertEquals(false, stopWatch.isRecording());
        assertEquals(true, stopWatch.isPaused());
        assertEquals(false, stopWatch.isStopped());

        assertEquals(false, stopWatch.isStartable());
        assertEquals(false, stopWatch.isPausable());
        assertEquals(true, stopWatch.isResumable());
        assertEquals(true, stopWatch.isStoppable());

        assertEquals(State.PAUSED, stopWatch.getState());

    }

    @Test
    void testStoppedState() {
        StopWatch stopWatch = StopWatch.create();
        stopWatch.start();
        stopWatch.stop();

        assertEquals(false, stopWatch.isRecording());
        assertEquals(false, stopWatch.isPaused());
        assertEquals(true, stopWatch.isStopped());

        assertEquals(true, stopWatch.isStartable());
        assertEquals(false, stopWatch.isPausable());
        assertEquals(false, stopWatch.isResumable());
        assertEquals(false, stopWatch.isStoppable());

        assertEquals(State.STOPPED, stopWatch.getState());

    }


}
