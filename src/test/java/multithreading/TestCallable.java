package multithreading;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import lombok.SneakyThrows;
import lombok.ToString;

@ToString(callSuper = true)
public class TestCallable<V> extends TestTask implements Callable<V> {

    private final Callable<V> callable;

    TestCallable(Callable<V> callable) {
        this.callable = callable;
    }

    @SneakyThrows
    @Override
    public V call() throws Exception {

        start();

        try {
            return callable.call();
        } catch (InterruptedException | CancellationException exception) {
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
        return null;
    }

}
