package com.ogawa.fico.performance.logging.executeable;

import com.ogawa.fico.function.Function3;
import com.ogawa.fico.performance.logging.ActionLogger;
import java.util.function.BiPredicate;
import java.util.function.Function;
import lombok.NonNull;
import lombok.SneakyThrows;

public class LoggingFunction<T, R> extends LoggingAction implements Function<T, R> {

    private final Function<T, R> function;
    private final BiPredicate<T, R> adjustmentBiPredicate;

    private final Function3<Function<T, R>, T, R, R> adjustmentFunction3;

    private final Function3<Function<T, R>, T, Exception, R> errorCorrectionFunction3;

    private Function3<Function<T, R>, T, Exception, R> errorCorrector;

    private Function3<Function<T, R>, T, R, R> adjuster;

    public LoggingFunction(@NonNull final ActionLogger actionLogger, @NonNull final Function<T, R> function,
        final String namedResult, final String[] namedArguments) {
        this(actionLogger, function, null, null, null,
            namedResult, namedArguments
        );
    }

    LoggingFunction(@NonNull final ActionLogger actionLogger,
        @NonNull final Function<T, R> function,
        BiPredicate<T, R> adjustmentBiPredicate,
        Function3<Function<T, R>, T, R, R> adjustmentFunction3,
        Function3<Function<T, R>, T, Exception, R> errorCorrectionFunction3,
        final String namedResult, final String[] namedArguments) {
        super(actionLogger, namedResult, 1, namedArguments);

        this.function = function;
        this.adjustmentBiPredicate = adjustmentBiPredicate;
        this.adjustmentFunction3 = adjustmentFunction3;
        this.errorCorrectionFunction3 = errorCorrectionFunction3;

        if (adjustmentFunction3 != null) {
            if (adjustmentBiPredicate == null) {
                this.adjuster = this::adjustUnconditionally;
            } else {
                this.adjuster = this::adjustConditionally;
            }
        } else {
            if (adjustmentBiPredicate != null) {
                throw new IllegalStateException(
                    "adjustmentBiPredicate is not null, but adjustmentTriFunction is null");
            } else {
                this.adjuster = this::keep;
            }
        }

        if (errorCorrectionFunction3 == null) {
            this.errorCorrector = this::throwException;
        } else {
            this.errorCorrector = this::correctError;
        }

    }

    @SneakyThrows
    private R throwException(Function<T, R> function, T functionArgument, Exception exception) {
        throw exception;
    }

    private R correctError(Function<T, R> function, T originalArgument, Exception caughtException) {
        actionLogger.announceAdjustment();
        R result = errorCorrectionFunction3.apply(function, originalArgument, caughtException);
        registerNamedResultValue(result);
        return result;
    }

    private R keep(Function<T, R> function, T originalArgument, R originalResult) {
        return originalResult;
    }

    private R adjustConditionally(Function<T, R> function, T originalArgument, R originalResult) {
        if (adjustmentBiPredicate.test(originalArgument, originalResult)) {
            actionLogger.announceAdjustment();
            originalResult = adjustmentFunction3.apply(function, originalArgument, originalResult);
            registerNamedResultValue(originalResult);
        }
        return originalResult;
    }

    private R adjustUnconditionally(Function<T, R> function, T originalArgument, R originalResult) {
        actionLogger.announceAdjustment();
        originalResult = adjustmentFunction3.apply(function, originalArgument, originalResult);
        registerNamedResultValue(originalResult);
        return originalResult;
    }

    /**
     * Calls the target consumer and notifies the progress logger the step's initiation, success or failure.
     *
     * @param functionArgument the input argument for the target consumer
     */
    @Override
    public R apply(T functionArgument) {
        R result;
        try {
            registerNamedArgumentValues(functionArgument);
            actionLogger.announceStep();
            result = function.apply(functionArgument);
            registerNamedResultValue(result);
            result = adjuster.apply(function, functionArgument, result);
            confirmSuccess();
        } catch (Exception exception) {
            handleException(exception);
            result = errorCorrector.apply(function, functionArgument, exception);
        }
        return result;
    }

}
