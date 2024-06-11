package com.ogawa.fico.performance.logging.building.builder;

import com.ogawa.fico.performance.logging.building.CommonCurrentStageBuilder;
import com.ogawa.fico.performance.logging.executeable.LoggingOneCallRunnable;

public class LoggingRunnableBuilder extends LoggingActionBaseBuilder<LoggingRunnableBuilder>
    implements CommonCurrentStageBuilder<LoggingRunnableBuilder, LoggingOneCallRunnable> {

    Runnable runnable;

    LoggingRunnableBuilder(AbstractLoggingActionBuilder abstractLoggingActionBuilder, Runnable runnable) {
        super(abstractLoggingActionBuilder);
        this.runnable = runnable;
    }

    @Override
    public LoggingRunnableBuilder clone() {
        return new LoggingRunnableBuilder(this, runnable);
    }

    public LoggingOneCallRunnable build() {
        return new LoggingOneCallRunnable(createActionLogger(), runnable);
    }
}
