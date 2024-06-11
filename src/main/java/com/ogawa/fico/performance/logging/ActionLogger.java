package com.ogawa.fico.performance.logging;

import com.ogawa.fico.performance.measuring.Progress;
import com.ogawa.fico.performance.measuring.StopWatch;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.slf4j.Logger;

@ToString(callSuper = true)
public class ActionLogger extends ActionLoggerDefinition implements AutoCloseable {

    /**
     * Return the number of the current batch. Batch numbers start with 1. If no batch is running, 0 is returned.
     *
     * @return the number of the current batch
     */
    @Getter
    private long batchNo;
    /**
     * Return the number of the current step. Step numbers start with 1. If not started, 0 is returned.
     *
     * @return the number of the current step
     */
    @Getter
    private long stepNumber;
    @Getter
    private long stepUnits;

    @Getter
    private StopWatch stepStopWatch;

    @Getter
    private Progress totalProgress;
    @Getter
    private Progress batchProgress;

    @Getter
    private Throwable throwable;

    public ActionLogger(ActionLoggerDefinition actionLoggerDefinition) {
        super(actionLoggerDefinition);
        reset();
    }

    public void reset() {
        totalProgress = Progress.ZERO;
        batchProgress = Progress.ZERO;
        stepStopWatch = StopWatch.create();
        stepStopWatch.pause();
        throwable = null;
        stepNumber = 0L;
        batchNo = 0L;
    }

    public void setVariable(String name, Object value) {
        this.messagePreparator.addVariable(false, name, value);
    }

    public void setVariable(String name, Supplier<?> supplier) {
        this.messagePreparator.addVariable(false, name, supplier);
    }

    /**
     * return true, if a batch size was reached and the batch is full
     *
     * @return true if the batch is reached and full, else false
     */
    private boolean isBatchComplete() {
        return (batchSize != 0L) && ((batchProgress.getProcessedUnits() >= batchSize));
    }

    private void log(ActionMessageTemplate messageTemplate) {
        if (logger.isEnabledForLevel(messageTemplate.getLogLevel())) {

            String message = messageTemplate.getPreparedMessageTemplate().format(this);

            // levels in order of probability of occurrence
            switch (messageTemplate.getLogLevel()) {
                case INFO:
                    logger.info(message);
                    break;
                case WARN:
                    logger.warn(message);
                    break;
                case DEBUG:
                    logger.debug(message);
                    break;
                case TRACE:
                    logger.trace(message);
                    break;
                case ERROR:
                    logger.error(message);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown log level: " + messageTemplate.getLogLevel());
            }
        }
    }

    private void log(ActionMessageTemplate[] messageTemplates) {
        for (ActionMessageTemplate messageTemplate : messageTemplates) {
            log(messageTemplate);
        }
    }

    /**
     * Notify for the call of the progressor
     */
    public void announceStep() {

        stepStopWatch.resume();
        stepNumber += 1L;

        // Is a new batch starting?
        if (batchProgress.getProcessedUnits() == 0L) {

            // very first call?
            if (totalProgress.getProcessedUnits() == 0L) {
                log(batchMessageSetDefinition.getBeforeMessageTemplates());
            }

            log(batchGroupCompleteMessageSetDefinition.getBeforeMessageTemplates());

        }

        log(invocationMessageSetDefinition.getBeforeMessageTemplates());

    }

    /**
     * Register the executed processor call to add it to the progress. Do not call, if there is no progress
     */
    public void confirmStepSuccess(long units) {
        stepStopWatch.pause();
        stepUnits = units;
        batchProgress = batchProgress.plus(stepStopWatch.getLastRecordedTime(), units);
        totalProgress = totalProgress.plus(stepStopWatch.getLastRecordedTime(), units);

        log(invocationMessageSetDefinition.getAfterMessageTemplates());

        if (isBatchComplete()) {
            log(batchGroupCompleteMessageSetDefinition.getAfterMessageTemplates());
            batchProgress = Progress.ZERO;
        }
    }

    public void announceAdjustment() {
        log(adjustmentMessageSetDefinition.getBeforeMessageTemplates());
    }

    public void confirmAdjustmentSuccess() {
        log(adjustmentMessageSetDefinition.getAfterMessageTemplates());
    }

    /**
     * Handle the throwable thrown by the progressor
     *
     * @param throwable Exception to handle
     */
    public void registerExceptionInStep(@NonNull Throwable throwable) {
        stepStopWatch.stop();
        this.throwable = throwable;
        batchProgress = batchProgress.plus(stepStopWatch.getLastRecordedTime());
        totalProgress = totalProgress.plus(stepStopWatch.getLastRecordedTime());
        log(invocationMessageSetDefinition.getExceptionMessageTemplates());
    }

    @Override
    public void close() throws Exception {
        if (throwable != null) {
            log(batchMessageSetDefinition.getExceptionMessageTemplates());
        } else {
            // pending rest to log (and no batchSize of 0 which is leading to div by zero)?
            if (batchSize > 0 && !isBatchComplete()) {
                batchProgress = batchProgress.plus(totalProgress.getProcessedUnits() % batchSize);
                log(batchGroupCompleteMessageSetDefinition.getAfterMessageTemplates());
            }
            log(batchMessageSetDefinition.getAfterMessageTemplates());
        }
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    public DateTimeFormatter getTimeFormatter() {
        return timeFormatter;
    }

    public DateTimeFormatter getDateFormatter() {
        return dateFormatter;
    }

    public DurationFormatter getDurationFormatter() {
        return durationFormatter;
    }

    public DecimalFormat getUnitDecimalFormat() {
        return unitDecimalFormat;
    }

    /**
     * Wrapped logger to log progress to
     */
    protected Logger logger;

    /**
     * Number of process steps to wait until a message is logged. 0 to log only start and end.
     */
    @Getter(AccessLevel.PUBLIC)
    protected long batchSize;

    @Getter(AccessLevel.PUBLIC)
    protected ChronoUnit throughputTimeUnit = ChronoUnit.SECONDS;


}
