package com.ogawa.fico.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class ApplicationError extends RuntimeException {

    @Getter
    @Setter(AccessLevel.PROTECTED)
    ErrorCode errorCode;

    ApplicationError(String message) {
        super(message);
    }

    ApplicationError(Throwable cause) {
        super(cause);
    }

    ApplicationError(String message, Throwable cause) {
        super(message, cause);
    }

}
