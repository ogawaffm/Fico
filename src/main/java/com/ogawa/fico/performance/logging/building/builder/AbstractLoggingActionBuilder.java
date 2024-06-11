package com.ogawa.fico.performance.logging.building.builder;

import static java.util.Collections.EMPTY_LIST;

import com.ogawa.fico.messagetemplate.MessagePreparator;
import com.ogawa.fico.messagetemplate.PreparedMessageTemplate;
import com.ogawa.fico.performance.logging.ActionLogger;
import com.ogawa.fico.performance.logging.ActionLoggerDefinition;
import com.ogawa.fico.performance.logging.ActionMessageTemplate;
import com.ogawa.fico.performance.logging.DurationFormatter;
import com.ogawa.fico.performance.logging.LogEvent;
import com.ogawa.fico.performance.logging.LogSubject;
import com.ogawa.fico.performance.logging.executeable.LoggingAction;
import com.ogawa.fico.performance.logging.ProtectedVariableAppenders;
import com.ogawa.fico.performance.logging.messageset.MessageSetDefinition;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.event.Level;

abstract class AbstractLoggingActionBuilder<CURR_BUILDER extends AbstractLoggingActionBuilder<CURR_BUILDER>>
    extends ActionLoggerDefinition {

    private Level defaultInvoctionLogLevel = Level.INFO;
    private Level defaultAdjustmentLogLevel = Level.INFO;
    private Level defaultErrorCorrectionLogLevel = Level.INFO;
    private Level defaultBatchGroupCompleteLogLevel = Level.INFO;
    private Level defaultBatchLogLevel = Level.INFO;
    private Level defaultAbortLogLevel = Level.INFO;
    private Level defaultErrorLogLevel = Level.ERROR;
    private Level defaultCloseLoggerLogLevel = Level.DEBUG;

    String[] arguments = null;

    String resultName;


    Map<String, Supplier> supplierVariables = new HashMap<>();

    AbstractLoggingActionBuilder() {
        super();
        messagePreparator = new MessagePreparator<ActionLogger>(
            ProtectedVariableAppenders.DEFAULT_VARIABLE_APPENDERS.values(), EMPTY_LIST
        );
    }

    AbstractLoggingActionBuilder(ActionLoggerDefinition actionLoggerDefinition) {
        super(actionLoggerDefinition);
    }

    AbstractLoggingActionBuilder(AbstractLoggingActionBuilder<CURR_BUILDER> abstractLoggingActionBuilder) {
        super(abstractLoggingActionBuilder);
        this.arguments = abstractLoggingActionBuilder.arguments;
        this.resultName = abstractLoggingActionBuilder.resultName;
        this.supplierVariables = new HashMap<>(abstractLoggingActionBuilder.supplierVariables);
        this.defaultInvoctionLogLevel = abstractLoggingActionBuilder.defaultInvoctionLogLevel;
        this.defaultAdjustmentLogLevel = abstractLoggingActionBuilder.defaultAdjustmentLogLevel;
        this.defaultErrorCorrectionLogLevel = abstractLoggingActionBuilder.defaultErrorCorrectionLogLevel;
        this.defaultBatchGroupCompleteLogLevel = abstractLoggingActionBuilder.defaultBatchGroupCompleteLogLevel;
        this.defaultErrorLogLevel = abstractLoggingActionBuilder.defaultErrorLogLevel;
        this.defaultCloseLoggerLogLevel = abstractLoggingActionBuilder.defaultCloseLoggerLogLevel;
    }

    ActionLogger createActionLogger() {
        ActionLogger actionLogger;

        if (logger == null) {
            throw new IllegalStateException("Logger must be set");
        }
        actionLogger = new ActionLogger(this);
        for (Map.Entry<String, Supplier> entry : supplierVariables.entrySet()) {
            actionLogger.setVariable(entry.getKey(), entry.getValue());
        }
        return actionLogger;
    }

    abstract public CURR_BUILDER clone();

    CURR_BUILDER logLevel(Level defaultInvoctionLogLevel) {
        this.defaultInvoctionLogLevel = defaultInvoctionLogLevel;
        return (CURR_BUILDER) this;
    }

    CURR_BUILDER adjustmentLogLevel(Level defaultAdjustmentLogLevel) {
        this.defaultAdjustmentLogLevel = defaultAdjustmentLogLevel;
        return (CURR_BUILDER) this;
    }

    CURR_BUILDER errorLogLevel(Level defaultErrorLogLevel) {
        this.defaultErrorLogLevel = defaultErrorLogLevel;
        return (CURR_BUILDER) this;
    }

    CURR_BUILDER abortLogLevel(Level defaultAbortLogLevel) {
        this.defaultAbortLogLevel = defaultAbortLogLevel;
        return (CURR_BUILDER) this;
    }

    CURR_BUILDER closeLoggerLogLevel(Level defaultCloseLoggerLogLevel) {
        this.defaultCloseLoggerLogLevel = defaultCloseLoggerLogLevel;
        return (CURR_BUILDER) this;
    }

    CURR_BUILDER batchGroupCompleteLogLevel(Level defaultBatchLogLevel) {
        this.defaultBatchGroupCompleteLogLevel = defaultBatchLogLevel;
        return (CURR_BUILDER) this;
    }

    CURR_BUILDER batchLogLevel(Level defaultBulkLogLevel) {
        this.defaultBatchLogLevel = defaultBulkLogLevel;
        return (CURR_BUILDER) this;
    }

    MessageSetDefinition getMessageSetDefinition(LogSubject logSubject) {
        switch (logSubject) {
            case INVOCATION:
                return invocationMessageSetDefinition;
            case ADJUSTMENT:
                return adjustmentMessageSetDefinition;
            case CORRECTION:
                return errorCorrectionMessageSetDefinition;
            case BATCH:
                return batchMessageSetDefinition;
            case BATCH_GROUP_COMPLETE:
                return batchGroupCompleteMessageSetDefinition;
            default:
                throw new IllegalArgumentException("Unknown log subject: " + logSubject);
        }
    }

    Level getDefaultLogLevel(LogSubject logSubject) {
        switch (logSubject) {
            case INVOCATION:
                return defaultInvoctionLogLevel;
            case ADJUSTMENT:
                return defaultAdjustmentLogLevel;
            case CORRECTION:
                return defaultErrorCorrectionLogLevel;
            case BATCH:
                return defaultBatchLogLevel;
            case BATCH_GROUP_COMPLETE:
                return defaultBatchGroupCompleteLogLevel;
            default:
                throw new IllegalArgumentException("Unknown log subject: " + logSubject);
        }
    }

    CURR_BUILDER logEvent(LogSubject logSubject, LogEvent logEvent, String messageTemplate) {
        Level level;
        if (logEvent == LogEvent.EXCEPTION) {
            level = defaultAbortLogLevel;
        } else if (logSubject == LogSubject.CORRECTION && logEvent == LogEvent.EXCEPTION) {
            level = defaultErrorLogLevel;
        } else {
            level = getDefaultLogLevel(logSubject);
        }
        return logEvent(logSubject, logEvent, level, messageTemplate);
    }

    CURR_BUILDER logEvent(LogSubject logSubject, LogEvent logEvent, Level level, String messageTemplate) {
        PreparedMessageTemplate preparedMessageTemplate = messagePreparator.prepare(messageTemplate);
        ActionMessageTemplate actionMessageTemplate = new ActionMessageTemplate(level, preparedMessageTemplate);

        MessageSetDefinition messageSetDefinition = getMessageSetDefinition(logSubject);

        messageSetDefinition.add(logEvent, level, messageTemplate);

        return (CURR_BUILDER) this;
    }

    CURR_BUILDER result(String resultName) {
        this.resultName = resultName;
        return (CURR_BUILDER) this;
    }

    CURR_BUILDER result() {
        return result(LoggingAction.DEFAULT_RESULT_NAME);
    }


    CURR_BUILDER arguments() {
        arguments = LoggingAction.DEFAULT_ARGUMENT_NAMES;
        return (CURR_BUILDER) this;
    }

    CURR_BUILDER arguments(String... names) {
        arguments = names;
        return (CURR_BUILDER) this;
    }

    CURR_BUILDER variable(String variableName, Object variableValue) {
        supplierVariables.put(variableName, () -> variableValue);
        return (CURR_BUILDER) this;
    }

    CURR_BUILDER variable(String variableName, Supplier variableValueSupplier) {
        supplierVariables.put(variableName, variableValueSupplier);
        return (CURR_BUILDER) this;
    }

    CURR_BUILDER batchSize(long batchSize) {
        this.batchSize = batchSize;
        return (CURR_BUILDER) this;
    }

    CURR_BUILDER throughputTimeUnit(ChronoUnit throughputTimeUnit) {
        this.throughputTimeUnit = throughputTimeUnit;
        return (CURR_BUILDER) this;
    }

    CURR_BUILDER dateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
        return (CURR_BUILDER) this;
    }

    CURR_BUILDER durationFormatter(DurationFormatter durationFormatter) {
        this.durationFormatter = durationFormatter;
        return (CURR_BUILDER) this;
    }

    CURR_BUILDER unitDecimalFormat(DecimalFormat unitDecimalFormat) {
        this.unitDecimalFormat = unitDecimalFormat;
        return (CURR_BUILDER) this;
    }

    CURR_BUILDER logger(Logger logger) {
        this.logger = logger;
        return (CURR_BUILDER) this;
    }

    <A, R> Supplier<R> createSupplier(Function<A, R> function, A argument) {
        return () -> function.apply(argument);
    }

//    <A, R> AbstractFunctionBuilder<A, R> invoke(Function<A, R> function) {
//        return new AbstractFunctionBuilder<A, R>(this);
//    }

    <R> LoggingSupplierBuilder invoke(Supplier<R> supplier) {
        return new LoggingSupplierBuilder<R>(this, supplier, null, null);
    }

    <A> LoggingConsumerBuilder invoke(Consumer<A> consumer) {
        return new LoggingConsumerBuilder<A>(this, consumer);
    }

    <V> LoggingCallableBuilder<V> invoke(Callable<V> callable) {
        return new LoggingCallableBuilder<V>(this, callable);
    }

    <V> LoggingRunnableBuilder invoke(Runnable runnable) {
        return new LoggingRunnableBuilder(this, runnable);
    }

}
