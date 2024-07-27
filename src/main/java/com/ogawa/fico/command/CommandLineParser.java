package com.ogawa.fico.command;

import com.ogawa.fico.application.Application;
import com.ogawa.fico.exception.MissingCommandError;
import com.ogawa.fico.exception.UnknownCommandError;
import java.util.HashMap;
import java.util.function.Function;

public class CommandLineParser {

    static private HashMap<String, Function<String[], Command>> knownCommands;

    static {
        knownCommands = new HashMap<>();

        knownCommands.put(ListCommand.KEY_WORD, ListCommand::new);
        knownCommands.put(ExportCommand.KEY_WORD, ExportCommand::new);
        knownCommands.put(HelpCommand.KEY_WORD, HelpCommand::new);

        knownCommands.put(RemoveCommand.KEY_WORD, RemoveCommand::new);
        knownCommands.put(AddCommand.KEY_WORD, AddCommand::new);
        knownCommands.put(AnalyzeCommand.KEY_WORD, AnalyzeCommand::new);

        knownCommands.put(DropCommand.KEY_WORD, DropCommand::new);
        knownCommands.put(CreateCommand.KEY_WORD, CreateCommand::new);
        knownCommands.put(ResetCommand.KEY_WORD, ResetCommand::new);

        knownCommands.put(StartCommand.KEY_WORD, StartCommand::new);
        knownCommands.put(StopCommand.KEY_WORD, StopCommand::new);
    }

    static String[] getAsStringArray(String string) {
        return new String[]{string};
    }

    public static Command parse(String[] commandLineArgs) {

        // check and process optional first @DatabaseName argument

        // is the optional database name argument present?
        if (commandLineArgs.length > 0 && commandLineArgs[0].startsWith("@")) {

            // sets the database name, even if there is no database name following the @
            Application.setDatabaseNameFromArgument(commandLineArgs[0].substring(1));

            // remove the @DatabaseName argument from the arguments
            commandLineArgs = removeFirstArgument(commandLineArgs);

        }

        if (commandLineArgs.length == 0) {
            throw new MissingCommandError("No command specified. Use 'help' for a list of commands.");
        }

        String commandName = commandLineArgs[0];

        if (!knownCommands.containsKey(commandName)) {
            throw new UnknownCommandError("Unknown command '" + commandName + "'. Use 'help' for a list of commands.");
        }

        // remove the command name from the arguments
        commandLineArgs = removeFirstArgument(commandLineArgs);

        // call constructor of the command class with the arguments belonging to the command
        return knownCommands.get(commandName).apply(commandLineArgs);

    }

    /**
     * Removes the first argument from the array of arguments.
     *
     * @param args The array of arguments.
     * @return A new array of arguments without the first argument.
     */
    public static String[] removeFirstArgument(String[] args) {

        if (args.length <= 1) {
            return new String[0];
        } else {

            String[] result = new String[args.length - 1];
            System.arraycopy(args, 1, result, 0, result.length);

            return result;
        }
    }

}
