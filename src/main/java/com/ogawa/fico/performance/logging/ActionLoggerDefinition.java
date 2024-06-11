package com.ogawa.fico.performance.logging;

import com.ogawa.fico.messagetemplate.MessagePreparator;
import com.ogawa.fico.performance.logging.messageset.MessageSetDefinition;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import lombok.NonNull;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.event.Level;

@ToString
public class ActionLoggerDefinition {

    protected DateTimeFormatter dateTimeFormatter;

    protected DateTimeFormatter timeFormatter;

    protected DateTimeFormatter dateFormatter;

    protected DurationFormatter durationFormatter;

    protected DecimalFormat unitDecimalFormat;

    protected MessagePreparator<ActionLogger> messagePreparator;

    /**
     * Wrapped logger to log progress to
     */
    protected Logger logger;

    /**
     * Number of process steps to wait until a message is logged. 0 to log only start and end.
     */
    protected long batchSize;

    protected ChronoUnit throughputTimeUnit = ChronoUnit.SECONDS;

    protected MessageSetDefinition invocationMessageSetDefinition = new MessageSetDefinition();
    protected MessageSetDefinition adjustmentMessageSetDefinition = new MessageSetDefinition();
    protected MessageSetDefinition errorCorrectionMessageSetDefinition = new MessageSetDefinition();

    protected MessageSetDefinition batchMessageSetDefinition = new MessageSetDefinition();
    protected MessageSetDefinition batchGroupCompleteMessageSetDefinition = new MessageSetDefinition();

    Level[][] defaultLogLevels = new Level[LogSubject.values().length][LogEvent.values().length];

    void setDefaultLogLevel(@NonNull LogSubject logSubject, @NonNull LogEvent logEvent, @NonNull Level level) {
        defaultLogLevels[logSubject.ordinal()][logEvent.ordinal()] = level;
    }

    Level getDefaultLogLevel(@NonNull LogSubject logSubject, @NonNull LogEvent logEvent) {
        return defaultLogLevels[logSubject.ordinal()][logEvent.ordinal()];
    }

    void setDefaultLogLevel(@NonNull LogSubject logSubject, @NonNull Level level) {
        for (int i = 0; i < LogEvent.values().length; i++) {
            defaultLogLevels[logSubject.ordinal()][i] = level;
        }
    }

    void setDefaultLogLevel(@NonNull LogEvent logEvent, @NonNull Level level) {
        for (int i = 0; i < LogSubject.values().length; i++) {
            defaultLogLevels[i][logEvent.ordinal()] = level;
        }
    }


    protected ActionLoggerDefinition() {
    }

    protected ActionLoggerDefinition(@NonNull ActionLoggerDefinition actionLoggerDefinition) {

        this.messagePreparator = actionLoggerDefinition.messagePreparator;

        this.logger = actionLoggerDefinition.logger;

        this.batchSize = actionLoggerDefinition.batchSize;

        this.invocationMessageSetDefinition = actionLoggerDefinition.invocationMessageSetDefinition;
        this.adjustmentMessageSetDefinition = actionLoggerDefinition.adjustmentMessageSetDefinition;
        this.errorCorrectionMessageSetDefinition = actionLoggerDefinition.errorCorrectionMessageSetDefinition;

        this.batchMessageSetDefinition = actionLoggerDefinition.batchMessageSetDefinition;
        this.batchGroupCompleteMessageSetDefinition = actionLoggerDefinition.batchGroupCompleteMessageSetDefinition;

        this.throughputTimeUnit = actionLoggerDefinition.throughputTimeUnit;

        if (actionLoggerDefinition.dateTimeFormatter != null) {
            this.dateTimeFormatter = actionLoggerDefinition.dateTimeFormatter;
        } else {
            this.dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        }

        if (actionLoggerDefinition.timeFormatter != null) {
            this.timeFormatter = actionLoggerDefinition.timeFormatter;
        } else {
            this.timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME;
        }

        if (actionLoggerDefinition.dateFormatter != null) {
            this.dateFormatter = actionLoggerDefinition.dateFormatter;
        } else {
            this.dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        }

        if (actionLoggerDefinition.durationFormatter != null) {
            this.durationFormatter = actionLoggerDefinition.durationFormatter;
        } else {
            this.durationFormatter = new DurationFormatter();
        }

        if (actionLoggerDefinition.unitDecimalFormat != null) {
            this.unitDecimalFormat = actionLoggerDefinition.unitDecimalFormat;
        } else {
            this.unitDecimalFormat = new DecimalFormat("#,##0");
        }

    }

}
