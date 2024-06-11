package com.ogawa.fico.messagetemplate;

import static com.ogawa.fico.messagetemplate.MessageTemplate.isValidVariableReference;

class MessageTemplateFragment {

    private final String text;

    MessageTemplateFragment(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public boolean isVariableReference() {
        return isValidVariableReference(text);
    }

}
