package com.ogawa.fico.command;

import com.ogawa.fico.application.Config;
import com.ogawa.fico.command.argument.ArgumentCardinality;
import com.ogawa.fico.exception.CommandLineError;
import com.ogawa.fico.exception.ExecutionException;
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

        if (!usesDatabase() && Config.isDatabaseSetByArgument()) {
            throw new CommandLineError("@" + Config.getDatabaseName() + " not allowed for " + getName() + " command.");
        }

        arguments = commandFollowingArgs;

        checkArgumentCardinality();

    }

    abstract boolean usesDatabase();

    String getDatabaseName() {
        return Config.getDatabaseName();
    }

    private void checkArgumentCardinality() {

        if (getMaxArgumentCount() == 0 && getArgumentCount() > 0) {
            throw new CommandLineError(getName() + " command does not take any arguments");
        }

        if (getMinArgumentCount() == getMaxArgumentCount() && getArgumentCount() != getMinArgumentCount()) {
            throw new CommandLineError(getName() + " command takes " + getMinArgumentCount() + " arguments"
                + " but got " + getArgumentCount() + " arguments");
        }

        if (getArgumentCount() > getMaxArgumentCount()) {
            throw new CommandLineError(getName() + " command takes at most " + getMaxArgumentCount() + " arguments"
                + " but got " + getArgumentCount() + " arguments");
        }

        if (getArgumentCount() < getMinArgumentCount()) {
            throw new CommandLineError(getName() + " command at least " + getMinArgumentCount() + " arguments"
                + " but got " + getArgumentCount() + " arguments");
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

    abstract void execute();

    String createMessage(String message) {
        return getName() + " failed." + (message != null && !message.isEmpty() ? " " + message : "");
    }

    void throwExecutionError(String message) {
        throw new ExecutionException(createMessage(message));
    }

    void throwExecutionError(Throwable cause) {
        throw new ExecutionException(createMessage(null), cause);
    }

    @Override
    public void run() {

        stopWatch = StopWatch.create();
        stopWatch.start();
        stopWatch.setName(getName());
        log.info("Started " + getName());

        execute();

        stopWatch.stop();

        log.info("Finished " + getName() + " in " + Formatter.format(stopWatch.getAccumulatedRecordedTime()));
    }

}
