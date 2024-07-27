package com.ogawa.fico.exception;

public class InvalidCommandArgument extends CommandLineError {

    public InvalidCommandArgument(String message) {
        super(message);
        setErrorCode(ErrorCode.INVALID_ARGUMENTS);
    }

    public InvalidCommandArgument(String message, Throwable cause) {
        super(message, cause);
        setErrorCode(ErrorCode.INVALID_ARGUMENTS);
    }

}
