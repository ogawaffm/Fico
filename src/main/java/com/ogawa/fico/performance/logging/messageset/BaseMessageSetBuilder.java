package com.ogawa.fico.performance.logging.messageset;

import com.ogawa.fico.performance.logging.LogEvent;
import lombok.NonNull;
import org.slf4j.event.Level;

public class BaseMessageSetBuilder<CURR_BUILDER extends BaseMessageSetBuilder<CURR_BUILDER>> extends
    MessageSetDefinition implements
    BaseMessageSetBuilderInterface<CURR_BUILDER> {

    public BaseMessageSetBuilder() {
        super();
    }

    public BaseMessageSetBuilder(@NonNull BaseMessageSetBuilder loggingInvocationBuilder) {
        super(loggingInvocationBuilder);
    }

    @Override
    public CURR_BUILDER log(LogEvent logEvent, Level logLevel, String messageTemplate) {
        add(logEvent, logLevel, messageTemplate);
        return (CURR_BUILDER) this;
    }

}
