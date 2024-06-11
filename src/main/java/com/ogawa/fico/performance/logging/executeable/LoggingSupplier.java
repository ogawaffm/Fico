package com.ogawa.fico.performance.logging.executeable;

import com.ogawa.fico.performance.logging.ActionLogger;
import java.util.function.Supplier;
import lombok.NonNull;

public class LoggingSupplier<T> extends LoggingAction implements Supplier<T> {

    private final Supplier<T> supplier;

    /**
     * Creates a new LoggingSupplier with the given ActionLogger and wrapped Supplier
     *
     * @param actionLogger ActionLogger
     * @param supplier     Wrapped Supplier
     */
    public LoggingSupplier(@NonNull final ActionLogger actionLogger, @NonNull final Supplier<T> supplier) {
        super(actionLogger);
        this.supplier = supplier;
    }

    /**
     * Supplier.getFromSupplier() implementation that calls the target supplier and notifies the progress logger
     *
     * @return a result
     */

    @Override
    public T get() {
        try {
            // announce the step to the progress logger
            actionLogger.announceStep();
            // call the target supplier
            T suppliedValue = supplier.get();
            registerNamedResultValue(suppliedValue);
            // confirm success because no logException was thrown
            confirmSuccess();
            return suppliedValue;
        } catch (Exception exception) {
            handleException(exception);
            // never gets here because handleException() always throws an logException
            return null;
        }
    }

}
