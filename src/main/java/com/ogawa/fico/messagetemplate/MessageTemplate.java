package com.ogawa.fico.messagetemplate;

import com.ogawa.fico.messagetemplate.appender.MessageFragmentAppender;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * I1 MessageTemplate represents a message that consists of simple text and variables. For example, such a message could
 * be <code>"Hello ${Username}!"</code> and evaluate as <code>"Hello John!"</code> or <code>"Hello Jane!"</code>.
 * Variables are implemented as {@link MessageFragmentAppender}.
 * <p></p>
 * I1 MassageTemplate is immutable and can be created by a {@link MessagePreparator}.
 */
public class MessageTemplate {

    /**
     * Array of fragments (text and template variables)
     */
    final protected MessageTemplateFragment[] messageTemplateFragments;

    public MessageTemplate(String messageTemplateText) {
        this.messageTemplateFragments = splitMessageTemplate(messageTemplateText);
    }

    private MessageTemplateFragment[] splitMessageTemplate(String messageTemplateText) {

        // Create list of possible fragments (text and template variables)
        List<MessageTemplateFragment> messageFragments = new ArrayList<>();

        MessageTemplateFragment messageTemplateFragment;

        Matcher matcher = MESSAGE_SPITTING_PATTERN.matcher(messageTemplateText);

        while (matcher.find()) {

            String match = matcher.group();

            messageTemplateFragment = new MessageTemplateFragment(match);

            messageFragments.add(messageTemplateFragment);

        }

        return messageFragments.toArray(new MessageTemplateFragment[0]);

    }

    public static void checkVariableName(String variableName) {
        if (!isValidVariableName(variableName)) {
            throw new IllegalArgumentException(VAR_NAMING_CONVENTION_MSG + " Variable name was: " + variableName);
        }
    }

    public static void checkVariableReference(String variableReference) {
        if (!isValidVariableReference(variableReference)) {
            throw new IllegalArgumentException(
                VAR_REF_NAMING_CONVENTION_MSG + " Variable reference was: " + variableReference
            );
        }
    }

    private static boolean isValidVariableName(String variableName) {
        return VAR_NAME_PATTERN.matcher(variableName).matches();
    }

    /**
     * Returns true if the given string is a variable reference, e.g. ${VariableName}
     *
     * @param variableReference Variable reference candidate to check
     * @return true if the given string is a variable reference, e.g. ${VariableName}
     */
    public static boolean isValidVariableReference(String variableReference) {
        return VAR_REF_PATTERN.matcher(variableReference).matches();
    }

    /**
     * Creates a variable reference, e.g. ${VariableName} from the given variable name
     *
     * @param variableName Variable name
     * @return Variable reference, e.g. ${VariableName}
     */
    public static String createVariableReference(String variableName) {
        checkVariableName(variableName);
        return "${" + variableName + "}";
    }

    /**
     * Returns the variable name from the given variable reference, e.g. VariableName from ${VariableName}
     *
     * @param variableReference Variable reference, e.g. ${VariableName}
     * @return Variable name, e.g. VariableName
     */
    public static String getNameFromVariableReference(String variableReference) {
        checkVariableReference(variableReference);
        return variableReference.substring(2, variableReference.length() - 1);
    }

    private static final String VAR_REF_CAPTURE_GRP_NAME = "VarRef";

    private static final String VAR_NAMING_CONVENTION_MSG =
        "Variable name must start with a letter and contain only letters, numbers and underscore (_).";

    private static final String VAR_REF_NAMING_CONVENTION_MSG =
        "Variable reference must start with ${ followed by the variable name and end with }." +
            VAR_NAMING_CONVENTION_MSG;

    private static final Pattern MESSAGE_SPITTING_PATTERN;

    private static final Pattern VAR_NAME_PATTERN;

    private static final Pattern VAR_REF_PATTERN;

    static {

        String VAR_NAME_PAT = "[a-z]\\w*";
        String VAR_REF_PAT = "\\$\\{" + VAR_NAME_PAT + "\\}";

        VAR_NAME_PATTERN = Pattern.compile(VAR_NAME_PAT, Pattern.CASE_INSENSITIVE);
        VAR_REF_PATTERN = Pattern.compile(VAR_REF_PAT, Pattern.CASE_INSENSITIVE);

        MESSAGE_SPITTING_PATTERN = Pattern.compile(
            // a variable reference
            "(?<" + VAR_REF_CAPTURE_GRP_NAME + ">" + VAR_REF_PAT + ")"
                // or text logBefore a variable reference
                + "|(.*?(?=" + VAR_REF_PAT + "))"
                // or text logAfter the last variable reference
                + "|(.+$)",
            Pattern.DOTALL + Pattern.CASE_INSENSITIVE
        );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (MessageTemplateFragment messageTemplateFragment : messageTemplateFragments) {
            sb.append((messageTemplateFragment).getText());
        }
        return sb.toString();
    }

}
