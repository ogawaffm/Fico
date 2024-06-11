package multithreading;

import java.util.concurrent.CancellationException;
import lombok.SneakyThrows;
import lombok.ToString;

@ToString(callSuper = true)
public class TestRunnable extends TestTask implements Runnable {

    private final Runnable runnable;

    TestRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    @SneakyThrows
    @Override
    public void run() {
        start();

        try {
            runnable.run();
        } catch (CancellationException exception) {
            handleException(exception);
        } catch (RuntimeException runtimeException) {
            handleException(runtimeException);
        } catch (Exception exception) {
            handleException(exception);
        } catch (Throwable throwable) {
            handleException(throwable);
        } finally {
            finish();
        }
    }
}
