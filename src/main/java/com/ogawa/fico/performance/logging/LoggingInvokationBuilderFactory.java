package com.ogawa.fico.performance.logging;

import com.ogawa.fico.performance.logging.messageset.ReturnerMessageSetBuilder;
import org.slf4j.event.Level;

public class LoggingInvokationBuilderFactory {

    private Level defaultActionLogLevel = Level.INFO;
    private Level defaultAdjustmentLogLevel = Level.INFO;
    private Level defaultErrorCorrectionLogLevel = Level.INFO;
    private Level defaultBatchLogLevel = Level.INFO;
    private Level defaultAbortLogLevel = Level.INFO;
    private Level defaultErrorLogLevel = Level.ERROR;
    private Level defaultCloseLoggerLogLevel = Level.DEBUG;

    private Level defaultInvocationLogLevel = Level.INFO;
    private Level defaultAdjustedLogLevel = Level.INFO;
    private Level defaultCorrectedLogLevel = Level.INFO;

    public ReturnerMessageSetBuilder create(LogSubject logSubject) {
        return new ReturnerMessageSetBuilder();
    }

}
