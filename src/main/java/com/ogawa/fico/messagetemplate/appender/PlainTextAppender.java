package com.ogawa.fico.messagetemplate.appender;

import com.ogawa.fico.messagetemplate.MessageTemplate;
import lombok.ToString;

/**
 * I1 MessageTemplateVariable represents a variable in a {@link MessageTemplate}. Its value can be resolved and appended
 * to a StringBuilder by its append-method. The value is resolved using a given context, which can be any object, which
 * provides the data for the variable. For example, the context could be a map, which provides the data for the variable
 * by its name. The variable name is provided by the getName-method.
 *
 * @param <CONTEXT>
 */
public class PlainTextAppender<CONTEXT> implements MessageFragmentAppender<CONTEXT> {

    private final String plainText;

    public PlainTextAppender(String plainText) {
        this.plainText = plainText;
    }

    @Override
    public void append(CONTEXT context, StringBuilder stringBuilder) {
        stringBuilder.append(plainText);
    }

    @Override
    public String toString() {
        return plainText;
    }

}
