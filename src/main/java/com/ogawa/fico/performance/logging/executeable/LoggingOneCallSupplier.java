package com.ogawa.fico.performance.logging.executeable;

import com.ogawa.fico.performance.logging.ActionLogger;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.SneakyThrows;

public class LoggingOneCallSupplier<T> extends LoggingSupplier<T> implements Supplier<T> {

    /**
     * Creates a new LoggingSupplier with the given ActionLogger and wrapped Supplier
     *
     * @param actionLogger    ActionLogger
     * @param wrappedSupplier Wrapped Supplier
     */
    public LoggingOneCallSupplier(@NonNull final ActionLogger actionLogger,
        @NonNull final Supplier<T> wrappedSupplier) {
        super(actionLogger, wrappedSupplier);
    }

    /**
     * Supplier.getFromSupplier() implementation that calls the target supplier and notifies the progress logger
     *
     * @return a result
     */

    @Override
    @SneakyThrows
    public T get() {
        try {
            actionLogger.reset();
            return super.get();
        } finally {
            actionLogger.close();
        }
    }

}
