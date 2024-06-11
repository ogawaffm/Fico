package com.ogawa.fico.performance.logging.executeable;

import com.ogawa.fico.performance.logging.ActionLogger;
import lombok.NonNull;

public class LoggingRunnable extends LoggingAction implements Runnable {

    private final Runnable action;

    /**
     * Creates a new LoggingConsumer with the given ActionLogger and wrapped Consumer
     *
     * @param actionLogger ActionLogger
     * @param runnable     Wrapped Runnable
     */

    public LoggingRunnable(@NonNull final ActionLogger actionLogger, @NonNull final Runnable runnable) {
        super(actionLogger);
        this.action = runnable;
    }

    /**
     * Calls the target runnable and notifies the progress logger the step's initiation, success or failure.
     */

    @Override
    public void run() {
        try {
            actionLogger.announceStep();
            action.run();
            confirmSuccess();
        } catch (Exception exception) {
            handleException(exception);
        }
    }
}
