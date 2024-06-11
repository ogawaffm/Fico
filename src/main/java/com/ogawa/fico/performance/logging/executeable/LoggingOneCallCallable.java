package com.ogawa.fico.performance.logging.executeable;

import com.ogawa.fico.performance.logging.ActionLogger;
import java.util.concurrent.Callable;
import lombok.NonNull;
import lombok.SneakyThrows;

public class LoggingOneCallCallable<V> extends LoggingCallable<V> implements Callable<V> {

    /**
     * Creates a new LoggingCallable with the given ActionLogger and wrapped Callable
     *
     * @param actionLogger ActionLogger
     * @param callable     Callable
     */
    public LoggingOneCallCallable(@NonNull final ActionLogger actionLogger, @NonNull final Callable<V> callable,
        String resultName) {
        super(actionLogger, callable, resultName);
    }

    @Override
    @SneakyThrows
    public V call() {
        try {
            actionLogger.reset();
            return super.call();
        } finally {
            actionLogger.close();
        }
    }

}
