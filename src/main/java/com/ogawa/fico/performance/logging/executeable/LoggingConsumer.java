package com.ogawa.fico.performance.logging.executeable;

import com.ogawa.fico.performance.logging.ActionLogger;
import java.util.function.Consumer;
import lombok.NonNull;

public class LoggingConsumer<T> extends LoggingAction implements Consumer<T> {

    private final Consumer<T> consumer;

    /**
     * Creates a new LoggingConsumer with the given ActionLogger and wrapped Consumer
     *
     * @param actionLogger ActionLogger
     * @param consumer     Wrapped Consumer
     */

    public LoggingConsumer(@NonNull final ActionLogger actionLogger, @NonNull final Consumer<T> consumer) {
        super(actionLogger);
        this.consumer = consumer;
    }

    public LoggingConsumer(@NonNull final ActionLogger actionLogger, @NonNull final Consumer<T> consumer,
        String[] namedArguments) {
        super(actionLogger, 1, namedArguments);
        this.consumer = consumer;
    }

    /**
     * Calls the target consumer and notifies the progress logger the step's initiation, success or failure.
     *
     * @param consumerArgument the input argument for the target consumer
     */
    @Override
    public void accept(T consumerArgument) {
        try {
            registerNamedArgumentValues(consumerArgument);
            actionLogger.announceStep();
            consumer.accept(consumerArgument);
            confirmSuccess();
        } catch (Exception exception) {
            handleException(exception);
        }
    }

}
