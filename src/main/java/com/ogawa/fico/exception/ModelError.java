package com.ogawa.fico.exception;

/**
 * Marker class for exceptions thrown for model errors.
 */

public class ModelError extends RuntimeException {

    public ModelError(String message) {
        super(message);
    }

    public ModelError(Throwable cause) {
        super(cause);
    }

}
