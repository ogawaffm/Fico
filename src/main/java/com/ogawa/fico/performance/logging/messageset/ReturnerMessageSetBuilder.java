package com.ogawa.fico.performance.logging.messageset;

import com.ogawa.fico.performance.logging.LogEvent;
import lombok.NonNull;
import org.slf4j.event.Level;

public class ReturnerMessageSetBuilder extends BaseMessageSetBuilder<ReturnerMessageSetBuilder> {

    public ReturnerMessageSetBuilder() {
        super();
    }

    public ReturnerMessageSetBuilder(@NonNull ReturnerMessageSetBuilder loggingInvokationBuilder) {
        super(loggingInvokationBuilder);
    }

    @Override
    public ReturnerMessageSetBuilder clone() {
        return new ReturnerMessageSetBuilder(this);
    }

    @Override
    public ReturnerMessageSetBuilder logBefore(String messageTemplate) {
        logBefore(Level.INFO, messageTemplate);
        add(LogEvent.BEFORE, Level.INFO, messageTemplate);
        return this;
    }

//    @Override
//    public ReturnerMessageSetBuilder logBefore(Level logLevel, String messageTemplate) {
//        add(LogEvent.BEFORE, logLevel, messageTemplate);
//        return this;
//    }

    @Override
    public ReturnerMessageSetBuilder logAfter(String messageTemplate) {
        add(LogEvent.AFTER, Level.INFO, messageTemplate);
        return this;
    }

    @Override
    public ReturnerMessageSetBuilder logAfter(Level logLevel, String messageTemplate) {
        add(LogEvent.AFTER, logLevel, messageTemplate);
        return this;
    }

    public ReturnerMessageSetBuilder logGood(String messageTemplate) {
        add(LogEvent.GOOD, Level.INFO, messageTemplate);
        return this;
    }

    public ReturnerMessageSetBuilder logGood(Level logLevel, String messageTemplate) {
        add(LogEvent.GOOD, logLevel, messageTemplate);
        return this;
    }

    public ReturnerMessageSetBuilder logPoor(String messageTemplate) {
        add(LogEvent.POOR, Level.INFO, messageTemplate);
        return this;
    }

    public ReturnerMessageSetBuilder logPoor(Level logLevel, String messageTemplate) {
        add(LogEvent.POOR, logLevel, messageTemplate);
        return this;
    }

    @Override
    public ReturnerMessageSetBuilder logException(String messageTemplate) {
        add(LogEvent.EXCEPTION, Level.INFO, messageTemplate);
        return this;
    }

    @Override
    public ReturnerMessageSetBuilder logException(Level logLevel, String messageTemplate) {
        add(LogEvent.EXCEPTION, logLevel, messageTemplate);
        return this;
    }

}
