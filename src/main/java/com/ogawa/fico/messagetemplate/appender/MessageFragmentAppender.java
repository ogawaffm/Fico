package com.ogawa.fico.messagetemplate.appender;

public interface MessageFragmentAppender<CONTEXT> {

    void append(CONTEXT context, StringBuilder stringBuilder);

}
