package com.ogawa.fico.exception;

/**
 * Marker class for exceptions thrown for command line errors.
 */
public abstract class CommandLineError extends ApplicationError {

    CommandLineError(String message) {
        this(message, null);
    }

    CommandLineError(Throwable cause) {
        super(cause);
    }

    CommandLineError(String message, Throwable cause) {
        super(message, cause);
    }

}
