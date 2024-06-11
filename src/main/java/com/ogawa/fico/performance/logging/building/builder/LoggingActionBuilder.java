package com.ogawa.fico.performance.logging.building.builder;

import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.ToString;

@ToString(callSuper = true)
public class LoggingActionBuilder extends LoggingBatchableBuilder<LoggingActionBuilder> {

    public LoggingActionBuilder() {
        super();
    }

    LoggingActionBuilder(AbstractLoggingActionBuilder<LoggingActionBuilder> abstractLoggingActionBuilder) {
        super(abstractLoggingActionBuilder);
    }

    public LoggingActionBuilder clone() {
        return new LoggingActionBuilder(this);
    }

    public <T, R> LoggingSupplierBuilder<R> invoke(Function<T, R> function, T argument) {
        return invoke(createSupplier(function, argument));
    }

    public <T> LoggingSupplierBuilder<T> invoke(Supplier<T> supplier) {
        return super.invoke(supplier);
    }

    public <V> LoggingCallableBuilder<V> invoke(Callable<V> callable) {
        return super.invoke(callable);
    }

    public LoggingRunnableBuilder invoke(Runnable runnable) {
        return super.invoke(runnable);
    }

    public LoggingReceiverBatchBuilder batch(Iterator iterator) {
        return new LoggingReceiverBatchBuilder(this);
    }

    public <RETURNER_RESULT> void batch(Predicate<RETURNER_RESULT> predicate) {

    }

}
