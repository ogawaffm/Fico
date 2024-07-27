package com.ogawa.fico.exception;

public class InvalidCommandArgumentNumber extends CommandLineError {

    public InvalidCommandArgumentNumber(String message) {
        super(message);
        setErrorCode(ErrorCode.INVALID_NUMBER_OF_COMMAND_ARGUMENTS);
    }

}
