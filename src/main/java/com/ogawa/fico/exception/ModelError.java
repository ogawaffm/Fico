package com.ogawa.fico.exception;

/**
 * Marker class for exceptions thrown for model errors.
 */

public class ModelError extends ApplicationError {

    public ModelError(String message) {
        super(message);
        setErrorCode(ErrorCode.DATABASE_MODEL_ERROR);
    }

    public ModelError(Throwable cause) {
        super(cause);
        setErrorCode(ErrorCode.DATABASE_MODEL_ERROR);
    }

}
