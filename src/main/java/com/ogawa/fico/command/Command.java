package com.ogawa.fico.command;

import com.ogawa.fico.application.Application;
import com.ogawa.fico.command.argument.ArgumentCardinality;
import com.ogawa.fico.exception.DatabaseArgumentNotAllowedError;
import com.ogawa.fico.exception.ExecutionError;
import com.ogawa.fico.exception.InvalidCommandArgumentNumber;
import com.ogawa.fico.performance.logging.Formatter;
import com.ogawa.fico.performance.measuring.StopWatch;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Command implements Runnable, ArgumentCardinality {

    StopWatch stopWatch;

    /**
     * The arguments of the command without the command name and the optional preceding database name.
     */
    @Getter
    private final String[] arguments;

    /**
     * @param commandFollowingArgs The arguments after the command name, may including an optional preceding database
     *                             name
     */
    Command(@NonNull String[] commandFollowingArgs) {

        if (!(this instanceof DatabaseCommand) && Application.isDatabaseSetByArgument()) {
            throw new DatabaseArgumentNotAllowedError(
                "@" + Application.getDatabaseName() + " not allowed for " + getName() + " command."
            );
        }
        arguments = commandFollowingArgs;

        checkArgumentCardinality();

    }

    public void checkArgumentCardinality() {

        if (!isAnyArgumentRequired() && getArgumentCount() == 0) {
            return;
        }

        if (getMaxGivenArgumentCount() == 0 && getArgumentCount() > 0) {
            throw new InvalidCommandArgumentNumber(getName() + " command does not accept any arguments");
        }

        if (getMinGivenArgumentCount() == getMaxGivenArgumentCount()
            && getArgumentCount() != getMinGivenArgumentCount()) {
            throw new InvalidCommandArgumentNumber(
                getName() + " command accepts "
                    + (!isAnyArgumentRequired() ? "no or " : "")
                    + getMinGivenArgumentCount() + " arguments "
                    + "but got " + getArgumentCount() + " arguments");
        }

        if (getArgumentCount() > getMaxGivenArgumentCount()) {
            throw new InvalidCommandArgumentNumber(
                getName() + " command accepts "
                    + (!isAnyArgumentRequired() ? "no or " : "")
                    + "at most " + getMaxGivenArgumentCount() + " arguments "
                    + "but got " + getArgumentCount() + " arguments");
        }

        if (getArgumentCount() < getMinGivenArgumentCount()) {
            throw new InvalidCommandArgumentNumber(
                getName() + " command accepts "
                    + (!isAnyArgumentRequired() ? "no or " : "")
                    + "at least " + getMinGivenArgumentCount() + "arguments "
                    + "but got " + getArgumentCount() + " arguments");
        }

    }

    public abstract String getName();

    /**
     * Returns one argument from the arguments of the command. The command name and the optional database name are no
     * arguments of the command and therefore cannot be retrieved. The first argument is at index 0.
     *
     * @param index The index of the argument to get, starting at 0.
     * @return The argument at the specified index.
     */
    public String getArgument(int index) {
        return arguments[index];
    }

    public int getArgumentCount() {
        return arguments == null ? 0 : arguments.length;
    }

    public boolean hasArguments() {
        return arguments != null && arguments.length > 0;
    }

    @Override
    public String toString() {
        return getName();
    }

    void beforeExecute() {
        stopWatch = StopWatch.create();
        stopWatch.start();
        stopWatch.setName(getName());
        log.info("Started {}", getName());
    }

    abstract void execute();

    void afterExecute() {
        stopWatch.stop();
        log.info("Finished {} in {}", getName(), Formatter.format(stopWatch.getAccumulatedRecordedTime()));
    }

    String createMessage(String message) {
        return getName() + " failed." + (message != null && !message.isEmpty() ? " " + message : "");
    }

    void throwExecutionError(String message) {
        throw new ExecutionError(createMessage(message));
    }

    void throwExecutionError(Throwable cause) {
        throw new ExecutionError(createMessage(null), cause);
    }

    @Override
    public void run() {
        beforeExecute();
        execute();
        afterExecute();
    }

}
