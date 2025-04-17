package com.ogawa.fico.command;

import static com.ogawa.fico.application.Config.getResource;

import com.ogawa.fico.application.Application;
import com.ogawa.fico.application.Config;
import com.ogawa.fico.command.argument.CommandWithNoArgs;
import com.ogawa.fico.exception.ErrorCode;
import com.ogawa.fico.exception.InvalidCommandArgumentNumber;

public class HelpCommand extends Command implements CommandWithNoArgs {

    final static public String KEY_WORD = "help";

    HelpCommand(String[] commandArguments) {
        super(commandArguments);
        if (hasArguments()) {
            throw new InvalidCommandArgumentNumber("help command does not take any arguments");
        }
    }

    public String getName() {
        return HelpCommand.KEY_WORD;
    }

    String getHelpText() {
        return getResource("txt/help.txt");
    }

    void execute() {
        System.out.print(Application.getName());
        System.out.print(" ");
        System.out.print(Application.getVersion());
        System.out.println(
            " - File comparator to scan a directory for files and log them to a database to find duplicates by checksums\n");
        System.out.println(getHelpText());
        System.out.println(
            "\nIn addition to the return value 0 for an error-free execution, the following error codes are defined\n");
        for (ErrorCode errorCode : ErrorCode.values()) {
            String code = "   " + errorCode.getCode();
            code = code.substring(code.length() - 3);
            System.out.println(code + " - " + errorCode.getName());
            System.out.println("      " + errorCode.getDescription());
            System.out.println();
        }
        System.out.println(
            "User-defined logging is applied by setting the " + Config.getEnvLogConfigFileVar() +
                " variable to a value that refers to a logback.xml-compliant configuration file.");
        System.out.print(
            "If the logging level is not specified in the configuration file, it can be specified by setting the "
                + Config.getEnvLogLevelVar() + " variable to ");
        System.out.println("ALL,TRACE, DEBUG, INFO, WARN, ERROR or OFF");
    }

    @Override
    public void run() {
        execute();
    }

}
