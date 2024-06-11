package com.ogawa.fico.messagetemplate;

import com.ogawa.fico.messagetemplate.appender.MessageFragmentAppender;
import java.util.Arrays;
import java.util.stream.Collectors;

public class PreparedMessageTemplate<CONTEXT> {

    final MessageFragmentAppender<CONTEXT>[] messageFragmentAppenders;

    PreparedMessageTemplate(MessageFragmentAppender<CONTEXT>[] messageFragmentAppenders) {
        this.messageFragmentAppenders = messageFragmentAppenders;
    }

    public String format(CONTEXT context) {
        StringBuilder stringBuilder = new StringBuilder();
        append(context, stringBuilder);
        return stringBuilder.toString();
    }

    public void append(CONTEXT context, StringBuilder stringBuilder) {
        for (MessageFragmentAppender<CONTEXT> messageFragmentAppender : messageFragmentAppenders) {
            messageFragmentAppender.append(context, stringBuilder);
        }
    }

    @Override
    public String toString() {
        // build the string representation of the message template by concatenating
        // the string representations of the message fragment appenders
        return Arrays.stream(messageFragmentAppenders).map(m -> m.toString()).collect(Collectors.joining());
    }

}
