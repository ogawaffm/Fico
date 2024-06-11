package com.ogawa.fico.performance.logging.messageset;

import static java.util.Collections.EMPTY_LIST;

import com.ogawa.fico.messagetemplate.MessagePreparator;
import com.ogawa.fico.messagetemplate.PreparedMessageTemplate;
import com.ogawa.fico.performance.logging.ActionLogger;
import com.ogawa.fico.performance.logging.ActionMessageTemplate;
import com.ogawa.fico.performance.logging.LogEvent;
import com.ogawa.fico.performance.logging.ProtectedVariableAppenders;
import java.util.Arrays;
import lombok.Getter;
import lombok.NonNull;
import org.slf4j.event.Level;

public class MessageSetDefinition {

    @Getter
    ActionMessageTemplate[] beforeMessageTemplates;
    @Getter
    ActionMessageTemplate[] afterMessageTemplates;
    @Getter
    ActionMessageTemplate[] goodMessageTemplates;
    @Getter
    ActionMessageTemplate[] poorMessageTemplates;
    @Getter
    ActionMessageTemplate[] exceptionMessageTemplates;

    final MessagePreparator<ActionLogger> messagePreparator;

    public MessageSetDefinition() {
        messagePreparator = new MessagePreparator<ActionLogger>(
            ProtectedVariableAppenders.DEFAULT_VARIABLE_APPENDERS.values(), EMPTY_LIST
        );
        beforeMessageTemplates = new ActionMessageTemplate[0];
        afterMessageTemplates = new ActionMessageTemplate[0];
        goodMessageTemplates = new ActionMessageTemplate[0];
        poorMessageTemplates = new ActionMessageTemplate[0];
        exceptionMessageTemplates = new ActionMessageTemplate[0];
    }

    MessageSetDefinition(@NonNull MessageSetDefinition messageSetDefinition) {
        messagePreparator = new MessagePreparator<ActionLogger>(
            ProtectedVariableAppenders.DEFAULT_VARIABLE_APPENDERS.values(), EMPTY_LIST
        );
        beforeMessageTemplates = messageSetDefinition.beforeMessageTemplates;
        afterMessageTemplates = messageSetDefinition.afterMessageTemplates;
        goodMessageTemplates = messageSetDefinition.goodMessageTemplates;
        poorMessageTemplates = messageSetDefinition.poorMessageTemplates;
        exceptionMessageTemplates = messageSetDefinition.exceptionMessageTemplates;

    }

    public ActionMessageTemplate[] getMessageTemplates(LogEvent logEvent) {
        switch (logEvent) {
            case BEFORE:
                return beforeMessageTemplates;
            case AFTER:
                return afterMessageTemplates;
            case GOOD:
                return goodMessageTemplates;
            case POOR:
                return poorMessageTemplates;
            case EXCEPTION:
                return exceptionMessageTemplates;
            default:
                throw new IllegalArgumentException("Unknown log event: " + logEvent);
        }
    }

    public void add(LogEvent logEvent, Level logLevel, String messageTemplate) {
        PreparedMessageTemplate<ActionLogger> preparedMessageTemplate = messagePreparator.prepare(messageTemplate);
        ActionMessageTemplate actionMessageTemplate = new ActionMessageTemplate(logLevel, preparedMessageTemplate);

        switch (logEvent) {
            case BEFORE:
                beforeMessageTemplates = addMessage(beforeMessageTemplates, actionMessageTemplate);
                break;
            case AFTER:
                afterMessageTemplates = addMessage(afterMessageTemplates, actionMessageTemplate);
                break;
            case GOOD:
                goodMessageTemplates = addMessage(goodMessageTemplates, actionMessageTemplate);
                break;
            case POOR:
                poorMessageTemplates = addMessage(poorMessageTemplates, actionMessageTemplate);
                break;
            case EXCEPTION:
                exceptionMessageTemplates = addMessage(exceptionMessageTemplates, actionMessageTemplate);
                break;
        }

    }

    static private ActionMessageTemplate[] addMessage(ActionMessageTemplate[] messageTemplates,
        ActionMessageTemplate messageTemplate) {
        if (messageTemplates == null) {
            messageTemplates = new ActionMessageTemplate[0];
        }
        messageTemplates = Arrays.copyOf(messageTemplates, messageTemplates.length + 1);
        messageTemplates[messageTemplates.length - 1] = messageTemplate;
        return messageTemplates;
    }

}
