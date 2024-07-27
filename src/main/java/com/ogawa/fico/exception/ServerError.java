package com.ogawa.fico.exception;

public class ServerError extends ApplicationError {

    public ServerError(String message) {
        super(message);
        setErrorCode(ErrorCode.SERVER_ERROR);
    }

    public ServerError(Throwable cause) {
        super(cause);
        setErrorCode(ErrorCode.SERVER_ERROR);
    }

    public ServerError(String message, Throwable cause) {
        super(message, cause);
        setErrorCode(ErrorCode.SERVER_ERROR);
    }

}
