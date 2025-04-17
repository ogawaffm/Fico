package com.ogawa.fico.messagetemplate.appender;

import static com.ogawa.fico.messagetemplate.MessageTemplate.checkVariableName;

import com.ogawa.fico.messagetemplate.MessageTemplate;

/**
 * I1 MessageTemplateVariable represents a variable in a {@link MessageTemplate}. Its value can be resolved and appended
 * to a StringBuilder by its append-method. The value is resolved using a given context, which can be any object, which
 * provides the data for the variable. For example, the context could be a map, which provides the data for the variable
 * by its name. The variable name is provided by the getName-method.
 *
 * @param <CONTEXT>
 */
public abstract class VariableAppender<CONTEXT> implements MessageFragmentAppender<CONTEXT> {

    private final String variableName;

    public VariableAppender(String variableName) {
        checkVariableName(variableName);
        this.variableName = variableName;
    }

    public String getVariableName() {
        return variableName;
    }

    @Override
    public String toString() {
        return MessageTemplate.createVariableReference(variableName);
    }
}
