package com.ogawa.fico.performance.logging.executeable;

import com.ogawa.fico.performance.logging.ActionLogger;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.SneakyThrows;

public class LoggingOneCallConsumer<T> extends LoggingConsumer<T> implements Consumer<T> {

    /**
     * Creates a new ProgressLoggingConsumerWrapper with the given ActionLogger and wrapped Consumer
     *
     * @param actionLogger    ActionLogger
     * @param wrappedConsumer Wrapped Consumer
     */

    public LoggingOneCallConsumer(@NonNull final ActionLogger actionLogger,
        @NonNull final Consumer<T> wrappedConsumer) {
        super(actionLogger, wrappedConsumer);
    }

    public LoggingOneCallConsumer(@NonNull final ActionLogger actionLogger,
        @NonNull final Consumer<T> wrappedConsumer, @NonNull final String[] namedArguments) {
        super(actionLogger, wrappedConsumer, namedArguments);
    }

    /**
     * Calls the target consumer and notifies the progress logger the step's initiation, success or failure.
     *
     * @param consumerArgument the input argument for the target consumer
     */
    @Override
    @SneakyThrows
    public void accept(T consumerArgument) {
        try {
            actionLogger.reset();
            super.accept(consumerArgument);
        } finally {
            actionLogger.close();
        }
    }

}
