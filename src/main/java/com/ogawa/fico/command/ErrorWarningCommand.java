package com.ogawa.fico.command;

public class ErrorWarningCommand extends HelpCommand {

    private String errorMessage;

    ErrorWarningCommand(String[] commandArguments) {
        super(null);
        if (commandArguments.length != 1) {
            throw new RuntimeException(getClass().getSimpleName() +
                " the error message as the one and only argument");
        }
        errorMessage = commandArguments[0];
    }

    @Override
    public String getName() {
        return "Error";
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void execute() {
        System.err.println("Command line error: " + getErrorMessage() + "\n");
        System.out.println(getHelpText());

    }
}
