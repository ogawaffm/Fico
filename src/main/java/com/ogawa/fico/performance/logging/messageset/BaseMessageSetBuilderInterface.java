package com.ogawa.fico.performance.logging.messageset;

import com.ogawa.fico.performance.logging.LogEvent;
import org.slf4j.event.Level;

public interface BaseMessageSetBuilderInterface<CURR_BUILDER> {

    CURR_BUILDER log(LogEvent logEvent, Level logLevel, String messageTemplate);

    default CURR_BUILDER logBefore(String messageTemplate) {
        return log(LogEvent.BEFORE, Level.INFO, messageTemplate);
    }

    default CURR_BUILDER logBefore(Level logLevel, String messageTemplate) {
        return log(LogEvent.BEFORE, logLevel, messageTemplate);
    }

    default CURR_BUILDER logAfter(String messageTemplate) {
        return log(LogEvent.AFTER, Level.INFO, messageTemplate);
    }

    default CURR_BUILDER logAfter(Level logLevel, String messageTemplate) {
        return log(LogEvent.AFTER, logLevel, messageTemplate);
    }

    default CURR_BUILDER logException(String messageTemplate) {
        return log(LogEvent.EXCEPTION, Level.INFO, messageTemplate);
    }

    default CURR_BUILDER logException(Level logLevel, String messageTemplate) {
        return log(LogEvent.EXCEPTION, logLevel, messageTemplate);
    }

}
