package com.ogawa.fico.performance.logging.executeable;

import com.ogawa.fico.function.Function3;
import com.ogawa.fico.performance.logging.ActionLogger;
import java.util.function.BiPredicate;
import java.util.function.Function;
import lombok.NonNull;
import lombok.SneakyThrows;

public class LoggingOneCallFunction<T, R> extends LoggingFunction<T, R> {

    /**
     * Creates a new ProgressLoggingConsumerWrapper with the given ActionLogger and wrapped Function
     *
     * @param actionLogger ActionLogger
     * @param function     Wrapped Function
     */

    public LoggingOneCallFunction(@NonNull final ActionLogger actionLogger,
        @NonNull final Function<T, R> function,
        BiPredicate<T, R> adjustmentBiPredicate,
        Function3<Function<T, R>, T, R, R> adjustmentTriFunction,
        Function3<Function<T, R>, T, Exception, R> errorCorrectionTriFunction,
        final String namedResult, final String[] namedArguments) {
        super(actionLogger, function, adjustmentBiPredicate, adjustmentTriFunction, errorCorrectionTriFunction,
            namedResult, namedArguments);
    }

    /**
     * Calls the target consumer and notifies the progress logger the step's initiation, success or failure.
     *
     * @param functionArgument the input argument for the target consumer
     */
    @SneakyThrows
    @Override
    public R apply(T functionArgument) {
        try {
            actionLogger.reset();
            return super.apply(functionArgument);
        } finally {
            actionLogger.close();
        }
    }

}
