package com.ogawa.fico.exception;

/**
 * Marker class for exceptions thrown for expectable errors.
 */
public class ExecutionException extends RuntimeException {

    public ExecutionException(String message) {
        super(message);
    }

    public ExecutionException(Throwable cause) {
        super(cause);
    }

    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
