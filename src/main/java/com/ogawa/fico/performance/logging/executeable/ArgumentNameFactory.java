package com.ogawa.fico.performance.logging.executeable;

public class ArgumentNameFactory {

    /**
     * The maximum number of arguments that can be used in a method signature see
     * <a href="http://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.3.3">Method Descriptors</a>
     */
    private static final int maxArguments = 255;
    private static String[] argumentNames = new String[1];
    private static String[][] argumentNamesArray = new String[0][];

    static private void checkArgumentNumber(int argumentNumber) {
        if (argumentNumber < 0) {
            throw new IllegalArgumentException("Argument number must be greater than or equal to 0");
        }

        if (argumentNumber > maxArguments) {
            throw new IllegalArgumentException("Argument number must be less than or equal to " + maxArguments);
        }
    }

    private static String getArgumentName(int argumentNumber) {
        // Is the argument out of the array?
        if (argumentNames.length < argumentNumber) {
            // yes, extend the array
            String[] extendedArgumentNames = new String[argumentNumber];
            // copy the old array to the new one
            System.arraycopy(argumentNames, 0, extendedArgumentNames, 0, argumentNames.length);
            argumentNames = extendedArgumentNames;
        }

        // Wasn't this argument name already created?
        if (argumentNames[argumentNumber] == null) {
            argumentNames[argumentNumber] = "Argument" + (argumentNumber + 1);
        }

        return argumentNames[argumentNumber];

    }

    static String[] getArgumentNames(int argumentsNumber) {

        checkArgumentNumber(argumentsNumber);

        // Is the argument out of the array?
        if (argumentNamesArray.length < argumentsNumber) {
            // extend the array
            String[][] extendedArgumentNames = new String[argumentsNumber][];
            // copy the old array to the new one
            System.arraycopy(argumentNamesArray, 0, extendedArgumentNames, 0, argumentNamesArray.length);
            argumentNamesArray = extendedArgumentNames;
        }

        if (argumentNames[argumentsNumber - 1] == null) {
            for (int i = 0; i < argumentsNumber; i++) {
                argumentNamesArray[argumentsNumber - 1] = new String[argumentsNumber];
                argumentNamesArray[argumentsNumber - 1][i] = getArgumentName(i);
            }
        }

        return argumentNamesArray[argumentsNumber - 1];

    }

}
