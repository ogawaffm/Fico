package com.ogawa.fico.performance.logging.building.builder;

import com.ogawa.fico.performance.logging.building.ReturnerCurrentStageBuilder;
import com.ogawa.fico.performance.logging.executeable.LoggingCallable;
import com.ogawa.fico.performance.logging.executeable.LoggingOneCallCallable;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

public class LoggingCallableBuilder<CALLABLE_RESULT>
    extends LoggingActionBaseBuilder<LoggingCallableBuilder<CALLABLE_RESULT>> implements
    ReturnerCurrentStageBuilder<LoggingCallableBuilder<CALLABLE_RESULT>, CALLABLE_RESULT, Callable<CALLABLE_RESULT>> {

    private final Callable<CALLABLE_RESULT> callable;
    private Predicate<CALLABLE_RESULT> adjustmentQualityCheck;

    LoggingCallableBuilder(AbstractLoggingActionBuilder abstractLoggingActionBuilder,
        Callable<CALLABLE_RESULT> callable) {
        super(abstractLoggingActionBuilder);
        this.callable = callable;
    }

    public LoggingCallableBuilder<CALLABLE_RESULT> clone() {
        return new LoggingCallableBuilder(this, callable);
    }

    public LoggingOneCallCallable<CALLABLE_RESULT> build() {
        return new LoggingOneCallCallable<CALLABLE_RESULT>(createActionLogger(), callable, resultName);
    }

    public LoggingCallable<CALLABLE_RESULT> buildLooped(Callable<CALLABLE_RESULT> callable) {
        return new LoggingCallable<CALLABLE_RESULT>(createActionLogger(), callable, resultName);
    }

    @Override
    public LoggingCallableBuilder<CALLABLE_RESULT> check(Predicate<CALLABLE_RESULT> adjustmentQualityCheck) {
        this.adjustmentQualityCheck = adjustmentQualityCheck;
        return this;
    }

    @Override
    public LoggingCallableBuilder<CALLABLE_RESULT> result() {
        return super.result();
    }

    @Override
    public LoggingCallableBuilder<CALLABLE_RESULT> result(String resultName) {
        return super.result(resultName);
    }
}
