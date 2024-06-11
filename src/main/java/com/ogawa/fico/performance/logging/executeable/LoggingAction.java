package com.ogawa.fico.performance.logging.executeable;

import com.ogawa.fico.performance.logging.ActionLogger;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.ToString;
import java.util.ArrayList;

@ToString
public abstract class LoggingAction {

    final protected ActionLogger actionLogger;
    final protected String resultName;
    final protected String[] argumentNames;
    final protected int[] argumentNameIndexes;
    final protected int argumentNumber;

    protected int exceptionCount;

    public static final String NO_RESULT_NAME = null;
    public static final String DEFAULT_RESULT_NAME = "Result";
    public static final String[] DEFAULT_ARGUMENT_NAMES = null;
    public static final String[] NO_ARGUMENT_NAMES = new String[0];

    /**
     * Creates a new ProgressLoggingActionWrapper with the given ActionLogger and wrapped invoke
     *
     * @param actionLogger ActionLogger
     */
    LoggingAction(@NonNull final ActionLogger actionLogger) {
        this(actionLogger, NO_RESULT_NAME, 0, NO_ARGUMENT_NAMES);
    }

    LoggingAction(@NonNull final ActionLogger actionLogger, final String resultName) {
        this(actionLogger, resultName, 0, NO_ARGUMENT_NAMES);
    }

    LoggingAction(@NonNull final ActionLogger actionLogger, int argumentNumber, final String[] argumentNames) {
        this(actionLogger, NO_RESULT_NAME, argumentNumber, argumentNames);
    }

    LoggingAction(@NonNull final ActionLogger actionLogger,
        final String resultName, final int argumentNumber, final String[] argumentNames) {

        this.actionLogger = actionLogger;
        this.argumentNumber = argumentNumber;

        this.exceptionCount = 0;

        if (resultName != null && resultName.isEmpty()) {
            throw new IllegalArgumentException("resultName must not be empty");
        }

        this.resultName = resultName;

        if (argumentNumber < 0) {
            throw new IllegalArgumentException("argumentNumber must be non-negative, but got " + argumentNumber);
        }

        if (argumentNames == null) {

            this.argumentNames = ArgumentNameFactory.getArgumentNames(argumentNumber);
            this.argumentNameIndexes = IntStream.range(0, argumentNumber).toArray();

        } else {

            if (argumentNames.length > argumentNumber) {
                throw new IllegalArgumentException(
                    "Method signature naming mismatch. For a " + argumentNumber + " arguments method "
                        + argumentNames.length +
                        " argument names were defined: " + String.join(", ", argumentNames));
            }

            Set<String> argumentNameSet = new LinkedHashSet(argumentNames.length);
            List argumentNameIndexesList = new ArrayList(argumentNames.length);

            for (int i = 0; i < argumentNames.length; i++) {
                if (argumentNames[i] != null) {
                    if (argumentNameSet.add(argumentNames[i]) == false) {
                        throw new IllegalArgumentException("Duplicate named argument: " + argumentNames[i]);
                    }
                    argumentNameIndexesList.add(i);
                }
            }

            this.argumentNames = argumentNameSet.toArray(new String[argumentNameSet.size()]);
            this.argumentNameIndexes = argumentNameIndexesList.stream().mapToInt(i -> (int) i).toArray();

        }

    }

    protected void registerNamedArgumentValues(Object... args) {
        for (int i = 0; i < argumentNames.length; i++) {
            actionLogger.setVariable(argumentNames[i], args[argumentNameIndexes[i]]);
        }
    }

    protected void registerNamedResultValue(Object value) {
        if (resultName != null) {
            actionLogger.setVariable(resultName, value);
        }
    }

    /**
     * Override this method to confirm the success of the wrapped invoke with more than one unit.
     */
    protected void confirmSuccess() {
        actionLogger.confirmStepSuccess(1L);
    }

    @SneakyThrows
    protected void handleException(Exception exception) {

        actionLogger.registerExceptionInStep(exception);

        throw exception;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        if (resultName != null) {
            sb.append(resultName).append(" = ");
        }

        sb.append(super.getClass().getName()).append("(");

        for (int i = 0; i < argumentNumber; i++) {

            if (argumentNames.length < i) {
                sb.append("?");
            } else {
                sb.append(argumentNames[i]);
            }

            if (i < argumentNumber - 1) {
                sb.append(", ");
            }

        }

        return sb.append(")").toString();

    }

}
