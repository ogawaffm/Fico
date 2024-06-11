package com.ogawa.fico.messagetemplate.appender;

import static com.ogawa.fico.messagetemplate.MessageTemplate.createVariableReference;

import java.util.Map;

public class CustomVariableAppender<CONTEXT> extends VariableAppender<CONTEXT> {

    private final Map<String, VariableAppender<CONTEXT>> customVariableAppenders;

    public CustomVariableAppender(Map<String, VariableAppender<CONTEXT>> customVariableAppenders, String variableName) {
        super(variableName);
        this.customVariableAppenders = customVariableAppenders;
    }

    @Override
    public void append(CONTEXT context, StringBuilder stringBuilder) {
        // Is the variable set?
        if (customVariableAppenders.containsKey(getVariableName())) {
            // yes, append the variable
            customVariableAppenders.get(getVariableName()).append(context, stringBuilder);
        } else {
            // no, append the variable reference
            stringBuilder.append(createVariableReference(getVariableName()));
        }
    }
}
