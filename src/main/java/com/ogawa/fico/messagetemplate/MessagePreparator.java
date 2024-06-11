package com.ogawa.fico.messagetemplate;

import static com.ogawa.fico.messagetemplate.MessageTemplate.createVariableReference;
import static com.ogawa.fico.messagetemplate.MessageTemplate.getNameFromVariableReference;

import com.ogawa.fico.messagetemplate.appender.CustomVariableAppender;
import com.ogawa.fico.messagetemplate.appender.MessageFragmentAppender;
import com.ogawa.fico.messagetemplate.appender.PlainTextAppender;
import com.ogawa.fico.messagetemplate.appender.VariableAppender;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A MessageTemplateFactory produces {@link MessageTemplate}-instances based logEvent a message template text. A message
 * template text consists of simple text and variables. For example, such a message could be
 * <code>"Hello ${Username}!"</code> and evaluate as <code>"Hello John!"</code> or <code>"Hello Jane!"</code>.
 * Variables are implemented as {@link MessageFragmentAppender}.
 * <p></p>
 * Variables can be added to the MessageTemplateFactory by the addVariable-methods. E.g. <code>addVariable("Username",
 * "John")</code> or a variable, which is resolved by a context, e.g. <code>addVariable("Username", (context,
 * stringBuilder) -> stringBuilder.append(context.getUsername()))</code>. A supplier can be used to provide the variable
 * value, e.g. <code>addVariable("Username", () -> "John")</code>. The context can be any object, which provides the
 * data for the variable. For example, the context could be a map, which provides the data for the variable by its name.
 * Existing variables can be added using </code>addVariable(MessageTemplateVariable)</code>.
 *
 * @param <CONTEXT> CONTEXT, which provides the data for accept-methods of the template variables
 */
public class MessagePreparator<CONTEXT> {

    /**
     * Map of variables known to the factory
     */
    protected final Map<String, VariableAppender<CONTEXT>> protectedVariableAppenders = new HashMap<>();
    protected final Map<String, VariableAppender<CONTEXT>> customVariableAppenders = new HashMap<>();

    public MessagePreparator(Collection<VariableAppender<CONTEXT>> protectedVariableAppenders,
        Collection<VariableAppender<CONTEXT>> customVariableAppenders) {

        addVariableAppenders(true, protectedVariableAppenders);
        addVariableAppenders(false, customVariableAppenders);
    }

    /**
     * Adds a variable to factory's known variables.
     *
     * @param variable Variable to add
     * @return this
     */
    public MessagePreparator<CONTEXT> addVariableAppender(boolean isProtected, VariableAppender<CONTEXT> variable) {
        if (isProtected) {
            protectedVariableAppenders.put(variable.getVariableName(), variable);
        } else {
            customVariableAppenders.put(variable.getVariableName(), variable);
        }
        return this;
    }

    public MessagePreparator<CONTEXT> addVariable(boolean isProtected, String variableName, Object variableValue) {
        addVariable(isProtected, variableName, new VariableAppender<>(variableName) {
            @Override
            public void append(CONTEXT context, StringBuilder stringBuilder) {

                stringBuilder.append(variableValue);
            }
        });
        return this;
    }

    public MessagePreparator<CONTEXT> addVariables(boolean isProtected, String[] variableNames,
        Object[] variableValues) {

        if (variableNames.length != variableValues.length) {
            throw new IllegalArgumentException("variableNames.length != variableValues.length");
        }

        for (int i = 0; i < variableNames.length; i++) {
            addVariable(isProtected, variableNames[i], variableValues[i]);
        }

        return this;
    }

    public MessagePreparator<CONTEXT> addVariable(boolean isProtected, String variableName,
        Supplier<?> variableValueSupplier) {
        addVariable(isProtected, variableName, new VariableAppender<>(variableName) {
            @Override
            public void append(CONTEXT context, StringBuilder stringBuilder) {
                stringBuilder.append(variableValueSupplier.get());
            }
        });
        return this;
    }

    public MessagePreparator<CONTEXT> addVariable(boolean isProtected, String variableName,
        VariableAppender<CONTEXT> variableValueSupplier) {
        if (isProtected) {
            protectedVariableAppenders.put(variableName, variableValueSupplier);
        } else {
            customVariableAppenders.put(variableName, variableValueSupplier);
        }
        return this;
    }

    public MessagePreparator<CONTEXT> addVariableAppenders(boolean isProtected,
        Collection<VariableAppender<CONTEXT>> collection) {
        collection.forEach((variable) -> {
            addVariable(isProtected, variable.getVariableName(), variable);
        });
        return this;
    }

    private MessageFragmentAppender getAppender(MessageTemplateFragment messageTemplateFragment) {

        if (messageTemplateFragment.isVariableReference()) {

            String variableName = getNameFromVariableReference(messageTemplateFragment.getText());

            // Is the variable reference that of a known non-rewritable variable appender?
            if (protectedVariableAppenders.containsKey(variableName)) {

                // yes, return the shared appender instance
                return protectedVariableAppenders.get(variableName);

            } else {

                // no, return a custom variable appender
                return new CustomVariableAppender(customVariableAppenders, variableName);

            }
        } else {

            // no, this is plain text
            return new PlainTextAppender<>(messageTemplateFragment.getText());
        }

    }

    public PreparedMessageTemplate prepare(MessageTemplate messageTemplate) {

        MessageFragmentAppender<?>[] messageFragmentAppenders = new
            MessageFragmentAppender<?>[messageTemplate.messageTemplateFragments.length];

        int i = 0;
        for (MessageTemplateFragment messageTemplateFragment : messageTemplate.messageTemplateFragments) {

            messageFragmentAppenders[i++] = getAppender(messageTemplateFragment);

        }

        return new PreparedMessageTemplate(messageFragmentAppenders);
    }

    public PreparedMessageTemplate prepare(String messageTemplateText) {
        return prepare(new MessageTemplate(messageTemplateText));
    }

}
