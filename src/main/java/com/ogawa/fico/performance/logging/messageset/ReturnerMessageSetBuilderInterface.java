package com.ogawa.fico.performance.logging.messageset;

import com.ogawa.fico.performance.logging.LogEvent;
import org.slf4j.event.Level;

public interface ReturnerMessageSetBuilderInterface<CURR_BUILDER>
    extends BaseMessageSetBuilderInterface<CURR_BUILDER> {

    default CURR_BUILDER logGood(String messageTemplate) {
        return logGood(Level.INFO, messageTemplate);
    }

    default CURR_BUILDER logGood(Level logLevel, String messageTemplate) {
        return log(LogEvent.GOOD, logLevel, messageTemplate);
    }

    default CURR_BUILDER logPoor(String messageTemplate) {
        return logPoor(Level.INFO, messageTemplate);
    }

    default CURR_BUILDER logPoor(Level logLevel, String messageTemplate) {
        return log(LogEvent.POOR, logLevel, messageTemplate);
    }

}
