package com.ogawa.fico.command;

import static com.ogawa.fico.application.Config.getResource;

import com.ogawa.fico.command.argument.CommandWithNoArgs;
import com.ogawa.fico.exception.CommandLineError;

public class HelpCommand extends Command implements CommandWithNoArgs {

    final static public String KEY_WORD = "help";

    HelpCommand(String[] commandArguments) {
        super(commandArguments);
        if (hasArguments()) {
            throw new CommandLineError("help command does not take any arguments");
        }
    }

    @Override
    boolean usesDatabase() {
        return false;
    }

    public String getName() {
        return HelpCommand.KEY_WORD;
    }

    String getHelpText() {
        return getResource("txt/help.txt");
    }

    void execute() {
        System.out.println(
            "FiCo 1.0 - use the FiCo tool to scan a directory for files and log them to a database to find duplicates\n");
        System.out.println(getHelpText());
    }

    @Override
    public void run() {
        execute();
    }

}
