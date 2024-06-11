package com.ogawa.fico.performance.logging.building.builder;

import com.ogawa.fico.performance.logging.messageset.BaseMessageSetBuilderInterface;
import com.ogawa.fico.performance.logging.messageset.MessageSetDefinition;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * LoggingReceiverBatchBuilder NonReceivers (Runnable, Suppliers, Callables) are batched in this builder.
 */

public class LoggingNonReceiverBatchBuilder extends LoggingBatchableBuilder<LoggingNonReceiverBatchBuilder>
    implements BaseMessageSetBuilderInterface<LoggingNonReceiverBatchBuilder> {

    LoggingNonReceiverBatchBuilder(AbstractLoggingActionBuilder abstractLoggingActionBuilder) {
        super(abstractLoggingActionBuilder);
    }

    LoggingNonReceiverBatchBuilder(LoggingNonReceiverBatchBuilder loggingReceiverBatchBuilder) {
        super(loggingReceiverBatchBuilder);
    }

    MessageSetDefinition getContextMessageSetDefinition() {
        return this.batchMessageSetDefinition;
    }

    @Override
    public LoggingNonReceiverBatchBuilder clone() {
        return new LoggingNonReceiverBatchBuilder(this);
    }

    public LoggingNonReceiverBatchBuilder group(long batchSize) {
        return super.batchSize(batchSize);
    }

    public <T> LoggingSupplierBuilder<T> invoke(Supplier<T> supplier) {
        return super.invoke(supplier);
    }

    public <V> LoggingCallableBuilder<V> invoke(Callable<V> callable) {
        return super.invoke(callable);
    }

}
