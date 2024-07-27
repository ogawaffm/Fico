package com.ogawa.fico.exception;

public class MissingCommandError extends CommandLineError {

    public MissingCommandError(String message) {
        super(message);
        setErrorCode(ErrorCode.MISSING_COMMAND);
    }
}
