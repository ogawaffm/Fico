package com.ogawa.fico.performance.logging.building.builder;

import com.ogawa.fico.performance.logging.ActionLoggerDefinition;
import com.ogawa.fico.performance.logging.DurationFormatter;
import com.ogawa.fico.performance.logging.LogEvent;
import com.ogawa.fico.performance.logging.messageset.MessageSetDefinition;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.event.Level;

abstract class LoggingActionBaseBuilder<CURR_BUILDER extends LoggingActionBaseBuilder<CURR_BUILDER>> extends
    AbstractLoggingActionBuilder<CURR_BUILDER> {

    LoggingActionBaseBuilder() {
        super();
    }

    LoggingActionBaseBuilder(ActionLoggerDefinition actionLoggerDefinition) {
        super(actionLoggerDefinition);
    }

    MessageSetDefinition getContextMessageSetDefinition() {
        return this.invocationMessageSetDefinition;
    }

    public CURR_BUILDER log(LogEvent logEvent, Level logLevel, String messageTemplate) {
        getContextMessageSetDefinition().add(logEvent, logLevel, messageTemplate);
        return (CURR_BUILDER) this;
    }

    @Override
    public CURR_BUILDER batchLogLevel(Level defaultBatchLogLevel) {
        return super.batchLogLevel(defaultBatchLogLevel);
    }

    @Override
    public CURR_BUILDER logLevel(Level defaultInvoctionLogLevel) {
        return super.logLevel(defaultInvoctionLogLevel);
    }

    public CURR_BUILDER batchGroupCompleteLogLevel(Level defaultBatchLogLevel) {
        return super.batchGroupCompleteLogLevel(defaultBatchLogLevel);
    }

    public CURR_BUILDER errorLogLevel(Level defaultErrorLogLevel) {
        return super.errorLogLevel(defaultErrorLogLevel);
    }

    public CURR_BUILDER adjustmentLogLevel(Level defaultAdjustmentLogLevel) {
        return super.errorLogLevel(defaultAdjustmentLogLevel);
    }

    public CURR_BUILDER abortLogLevel(Level defaultAbortLogLevel) {
        return super.abortLogLevel(defaultAbortLogLevel);
    }

    public CURR_BUILDER closeLoggerLogLevel(Level defaultCloseLoggerLogLevel) {
        return super.closeLoggerLogLevel(defaultCloseLoggerLogLevel);
    }

    public CURR_BUILDER variable(String variableName, Object variableValue) {
        return super.variable(variableName, variableValue);
    }

    public CURR_BUILDER variable(String variableName, Supplier variableValueSupplier) {
        return super.variable(variableName, variableValueSupplier);
    }

    public CURR_BUILDER throughputTimeUnit(ChronoUnit throughputTimeUnit) {
        return super.throughputTimeUnit(throughputTimeUnit);
    }

    public CURR_BUILDER dateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
        return super.dateTimeFormatter(dateTimeFormatter);
    }

    public CURR_BUILDER durationFormatter(DurationFormatter durationFormatter) {
        return super.durationFormatter(durationFormatter);
    }

    public CURR_BUILDER unitDecimalFormat(DecimalFormat unitDecimalFormat) {
        return super.unitDecimalFormat(unitDecimalFormat);
    }

    public CURR_BUILDER logger(Logger logger) {
        return super.logger(logger);
    }

}
