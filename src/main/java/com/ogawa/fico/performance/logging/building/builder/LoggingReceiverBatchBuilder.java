package com.ogawa.fico.performance.logging.building.builder;

import com.ogawa.fico.performance.logging.messageset.BaseMessageSetBuilderInterface;
import com.ogawa.fico.performance.logging.messageset.MessageSetDefinition;
import java.util.function.Consumer;
import java.util.function.Function;

public class LoggingReceiverBatchBuilder extends LoggingBatchableBuilder<LoggingReceiverBatchBuilder>
    implements BaseMessageSetBuilderInterface<LoggingReceiverBatchBuilder> {

    LoggingReceiverBatchBuilder(AbstractLoggingActionBuilder abstractLoggingActionBuilder) {
        super(abstractLoggingActionBuilder);
    }

    LoggingReceiverBatchBuilder(LoggingReceiverBatchBuilder loggingNoneReceiverBatchBuilder) {
        super(loggingNoneReceiverBatchBuilder);
    }

    MessageSetDefinition getContextMessageSetDefinition() {
        return this.batchMessageSetDefinition;
    }

    @Override
    public LoggingReceiverBatchBuilder clone() {
        return new LoggingReceiverBatchBuilder(this);
    }

    @Override
    public <T, R> AbstractFunctionBuilder<T, R> invoke(Function<T, R> function) {
        return super.invoke(function);
    }

    public <T> LoggingConsumerBuilder<T> invoke(Consumer<T> consumer) {
        return super.invoke(consumer);
    }

}
