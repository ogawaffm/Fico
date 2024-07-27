package com.ogawa.fico.exception;

public class UnknownCommandError extends CommandLineError {

    public UnknownCommandError(String message) {
        super(message);
        setErrorCode(ErrorCode.UNKNOWN_COMMAND);
    }

    public UnknownCommandError(Throwable cause) {
        super(cause);
        setErrorCode(ErrorCode.UNKNOWN_COMMAND);
    }

}
