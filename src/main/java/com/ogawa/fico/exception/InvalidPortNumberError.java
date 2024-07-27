package com.ogawa.fico.exception;

public class InvalidPortNumberError extends CommandLineError {

    public InvalidPortNumberError(String message) {
        super(message);
        setErrorCode(ErrorCode.DATABASE_PORT_ERROR);
    }

    public InvalidPortNumberError(Throwable cause) {
        super(cause);
        setErrorCode(ErrorCode.DATABASE_PORT_ERROR);
    }

    public InvalidPortNumberError(String message, Throwable cause) {
        super(message, cause);
        setErrorCode(ErrorCode.DATABASE_PORT_ERROR);
    }
}
