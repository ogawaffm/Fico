package multithreading;

import utility.TestUtils;

public class TestTaskFactory {

    /* ************************************************************************************************************* */
    /* ************************************************** Runnable ************************************************* */
    /* ************************************************************************************************************* */

    public static TestRunnable createWaitingRunnable(int secondsToWait) {
        return new TestRunnable(() -> TestUtils.waitSeconds(secondsToWait));
    }

    public static TestRunnable createWorkingRunnable(int secondsToWork) {
        return new TestRunnable(() -> TestUtils.workSeconds(secondsToWork));
    }

    public static TestRunnable createRuntimeExceptionThrowingRunnable(int afterSeconds, Throwable throwable) {
        return new TestRunnable(() -> {
            TestUtils.workSeconds(afterSeconds);
            throw new RuntimeException(throwable);
        });
    }

    public static TestRunnable createInterruptingRunnable() {
        return new TestRunnable(() -> Thread.currentThread().interrupt());
    }

    /* ************************************************************************************************************* */
    /* ************************************************** Callable ************************************************* */
    /* ************************************************************************************************************* */

    public static <V> TestCallable<V> createNullReturningWaitingCallable(int secondsToWait) {
        return new TestCallable<V>(() -> {
            TestUtils.waitSeconds(secondsToWait);
            return null;
        });
    }

    public static <V> TestCallable<V> createNullReturningWorkingCallable(int secondsToWork) {
        return new TestCallable<V>(() -> {
            TestUtils.workSeconds(secondsToWork);
            return null;
        });
    }

    public static <V> TestCallable<V> createValueReturningWaitingCallable(int secondsToWait, V value) {
        return new TestCallable<V>(() -> {
            TestUtils.waitSeconds(secondsToWait);
            return value;
        });
    }

    public static <V> TestCallable<V> createValueReturningWorkingCallable(int secondsToWork, V value) {
        return new TestCallable<V>(() -> {
            TestUtils.workSeconds(secondsToWork);
            return value;
        });
    }

    public static <V> TestCallable<V> createRuntimeExceptionThrowingWaitingCallable(int secondsToWait,
        Throwable throwable) {
        return new TestCallable<V>(() -> {
            TestUtils.waitSeconds(secondsToWait);
            throw new RuntimeException(throwable);
        });
    }

}
