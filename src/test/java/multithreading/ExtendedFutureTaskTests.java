package multithreading;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ogawa.fico.multithreading.ExtendedFutureTask;
import com.ogawa.fico.multithreading.ThreadUtils;
import java.time.LocalDateTime;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

public class ExtendedFutureTaskTests {

    private int taskExecutionTimeInSec = 2;

    private <V> ExtendedFutureTask createRunnableExtendedFutureTask(V value) {
        return new ExtendedFutureTask<>(TestTaskFactory.createWorkingRunnable(taskExecutionTimeInSec));
    }

    private <V> ExtendedFutureTask<V> createCallableExtendedFutureTask(V value) {
        return new ExtendedFutureTask<>(
            TestTaskFactory.createValueReturningWaitingCallable(taskExecutionTimeInSec, value));
    }

    <V> void testLifeCycleDateTimes(Function<V, ExtendedFutureTask<V>> extendedFutureTaskFactory, V value) {

        LocalDateTime before, after, creationTime, startTime, finishTime;

        ExtendedFutureTask eft;

        // ******** creation ********

        before = LocalDateTime.now();
        eft = extendedFutureTaskFactory.apply(value);
        after = LocalDateTime.now();

        // save creationTime for later comparison
        creationTime = eft.getCreationTime();

        // a valid creationTime must be set
        assertNotNull(creationTime);

        // creationTime must be between logBefore and logAfter inclusive
        assertFalse(before.isAfter(creationTime));
        assertFalse(after.isBefore(creationTime));

        // because the task is not started yet, the start and finish times must be null
        assertNull(eft.getStartTime());
        assertNull(eft.getFinishTime());

        // ******** start ********

        before = LocalDateTime.now();
        new Thread(eft).start();
        //  wait for the task to start
        ThreadUtils.waitSeconds(1);
        after = LocalDateTime.now();

        // save startTime for later comparison
        startTime = eft.getStartTime();

        // a valid startTime must be set
        assertNotNull(startTime);

        // startTime must be between logBefore and logAfter inclusive
        assertFalse(before.isAfter(startTime));
        assertFalse(after.isBefore(startTime));

        // creationTime must be logBefore or equal to startTime
        assertFalse(eft.getCreationTime().isAfter(eft.getStartTime()));

        finishTime = eft.getFinishTime();

        // because the task is not finished yet, the finish time must be null
        assertNull(eft.getFinishTime());

        // creationTime must not be changed by run()
        assertEquals(creationTime, eft.getCreationTime());

        // ******** running ********

        // the task must not be finished
        assertFalse(eft.isFinished());

        // the task must not be done
        assertFalse(eft.isDone());

        // the task must not be cancelled
        assertFalse(eft.isCancelled());

        // the task must not be failed
        assertFalse(eft.isFailed());

        // the task must not have a result
        assertFalse(eft.hasResult());

        // the task must not be interrupted
        assertFalse(eft.isInterrupted());

        // ******** finish ********

        // assure that the task is finished
        ThreadUtils.waitSeconds(taskExecutionTimeInSec + 1);

        // save finishTime for later comparison
        finishTime = eft.getFinishTime();

        // because the task is finished, the finish time must not be null
        assertNotNull(eft.getFinishTime());

        // finishTime must be between logBefore and logAfter inclusive
        assertFalse(startTime.isAfter(finishTime));
        assertFalse(LocalDateTime.now().isBefore(finishTime));

        // creationTime must not be changed by run()
        assertEquals(creationTime, eft.getCreationTime());

        // startTime must not be changed by run()
        assertEquals(startTime, eft.getStartTime());

        // finishTime must not be changed over time
        assertEquals(finishTime, eft.getFinishTime());

    }

    @Test
    void testRunnableLifeCycleDateTimes() {
        testLifeCycleDateTimes(this::createRunnableExtendedFutureTask, null);
    }

    @Test
    void testCallableLifeCycleDateTimes() {
        testLifeCycleDateTimes(this::createCallableExtendedFutureTask, null);
    }

    <V> void testCancelState(Function<V, ExtendedFutureTask<V>> extendedFutureTaskFactory, V value) {

        ExtendedFutureTask eft;

        // ******** creation ********

        TestRunnable runnable = TestTaskFactory.createWorkingRunnable(10);

        eft = new ExtendedFutureTask(runnable);

        System.out.println(LocalDateTime.now());
        new Thread(eft).start();

        TestTask.waitUntilStarted(runnable);
        System.out.println(LocalDateTime.now());

        assertTrue(!eft.isFinished());

        System.out.println(LocalDateTime.now());

        // ******** cancel ********
        eft.cancel(true);

        TestTask.waitUntilFinished(runnable);
        System.out.println(LocalDateTime.now());

        // the task must be cancelled
        assertTrue(eft.isCancelled());

        // the task must not be finished
        assertTrue(eft.isFinished());

        System.out.println(runnable);

    }

    @Test
    <V> void testRunnableCancelState() {
        testCancelState(this::createCallableExtendedFutureTask, null);
    }

    @Test
    <V> void testExceptionalState() {

        // ******** creation ********

        TestRunnable runnable = new TestRunnable(
            TestTaskFactory.createRuntimeExceptionThrowingRunnable(1, new Exception("MyException"))
        );

        ExtendedFutureTask eft = new ExtendedFutureTask(runnable);

        new Thread(eft).start();

        //  wait for the task to start
        TestTask.waitUntilStarted(runnable);

        // ******** cancel ********
        eft.cancel(true);

        // wait for the task to finish
        TestTask.waitUntilFinished(runnable);

        assertNotNull(runnable.throwable.get());

    }

}
