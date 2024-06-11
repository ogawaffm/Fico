package com.ogawa.fico.performance.logging.building.builder;

import com.ogawa.fico.performance.logging.LogEvent;
import com.ogawa.fico.performance.logging.executeable.LoggingOneCallConsumer;
import com.ogawa.fico.performance.logging.messageset.BaseMessageSetBuilderInterface;
import java.util.Iterator;
import java.util.function.Consumer;
import org.slf4j.event.Level;

public class LoggingConsumerBuilder<CONSUMER_ARG> extends LoggingActionBaseBuilder<LoggingConsumerBuilder<CONSUMER_ARG>>
    implements BaseMessageSetBuilderInterface<LoggingConsumerBuilder<CONSUMER_ARG>>,
    BuildingBuilder<LoggingOneCallConsumer<CONSUMER_ARG>> {

    private final Consumer<CONSUMER_ARG> consumer;

    LoggingConsumerBuilder(AbstractLoggingActionBuilder abstractLoggingActionBuilder, Consumer<CONSUMER_ARG> consumer) {
        super(abstractLoggingActionBuilder);
        this.consumer = consumer;
    }

    public LoggingConsumerBuilder<CONSUMER_ARG> clone() {
        return new LoggingConsumerBuilder<>(this, consumer);
    }

    public LoggingOneCallConsumer<CONSUMER_ARG> build() {
        return new LoggingOneCallConsumer<CONSUMER_ARG>(createActionLogger(), consumer);
    }

    public LoggingConsumerBuilder<CONSUMER_ARG> log(LogEvent logEvent, Level logLevel, String messageTemplate) {
        getContextMessageSetDefinition().add(logEvent, logLevel, messageTemplate);
        return this;
    }

    @Override
    public LoggingConsumerBuilder<CONSUMER_ARG> arguments() {
        return super.arguments();
    }

    @Override
    public LoggingConsumerBuilder<CONSUMER_ARG> arguments(String... names) {
        return super.arguments(names);
    }

    public Runnable batch(Iterator<CONSUMER_ARG> iterator) {
        Consumer<CONSUMER_ARG> consumer = build();
        return () -> {
            while (iterator.hasNext()) {
                consumer.accept(iterator.next());
            }
        };
    }
}
