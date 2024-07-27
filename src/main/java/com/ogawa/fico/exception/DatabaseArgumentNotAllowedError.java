package com.ogawa.fico.exception;

public class DatabaseArgumentNotAllowedError extends CommandLineError {

    public DatabaseArgumentNotAllowedError(String message) {
        super(message);
        setErrorCode(ErrorCode.DATABASE_ARGUMENT_NOT_ALLOWED);
    }

}
