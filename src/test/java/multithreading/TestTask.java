package multithreading;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;
import lombok.ToString;
import utility.TestUtils;

@ToString
public abstract class TestTask {

    final AtomicReference<LocalDateTime> started = new AtomicReference<>(null);

    final AtomicReference<LocalDateTime> finished = new AtomicReference<>(null);

    final AtomicReference<Throwable> throwable = new AtomicReference<>(null);

    protected void start() {
        started.set(LocalDateTime.now());
        finished.set(null);
    }

    protected void finish() {
        finished.set(LocalDateTime.now());
    }

    protected <T extends Throwable> void handleException(T throwable) throws T {
        this.throwable.set(throwable);
        throw (T) throwable;
    }

    static public void waitUntilStarted(TestTask testTask) {
        TestUtils.waitUntilNonNull(testTask.started);
    }

    static public void waitUntilFinished(TestTask testTask) {
        TestUtils.waitUntilNonNull(testTask.finished);
    }

}
