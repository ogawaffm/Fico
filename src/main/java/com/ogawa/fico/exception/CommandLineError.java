package com.ogawa.fico.exception;

/**
 * Marker class for exceptions thrown for command line errors.
 */
public class CommandLineError extends RuntimeException {

    public CommandLineError(String message) {
        super(message);
    }

    public CommandLineError(Throwable cause) {
        super(cause);
    }

}
