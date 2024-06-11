package com.ogawa.fico.performance.logging.executeable;

import com.ogawa.fico.performance.logging.ActionLogger;
import lombok.NonNull;
import lombok.SneakyThrows;

public class LoggingOneCallRunnable extends LoggingRunnable implements Runnable {

    /**
     * Creates a new ProgressLoggingConsumerWrapper with the given ActionLogger and wrapped Consumer
     *
     * @param actionLogger    ActionLogger
     * @param wrappedRunnable Wrapped Runnable
     */

    public LoggingOneCallRunnable(@NonNull final ActionLogger actionLogger, @NonNull final Runnable wrappedRunnable) {
        super(actionLogger, wrappedRunnable);
    }

    /**
     * Calls the target runnable and notifies the progress logger the step's initiation, success or failure.
     */
    @Override
    @SneakyThrows
    public void run() {
        try {
            actionLogger.reset();
            super.run();
        } finally {
            actionLogger.close();
        }
    }

}
