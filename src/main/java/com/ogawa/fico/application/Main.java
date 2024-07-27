package com.ogawa.fico.application;

import com.ogawa.fico.command.Command;
import com.ogawa.fico.exception.ApplicationError;
import com.ogawa.fico.exception.CommandLineError;
import com.ogawa.fico.command.CommandLineParser;
import com.ogawa.fico.exception.ErrorCode;
import com.ogawa.fico.exception.ExecutionError;
import com.ogawa.fico.exception.ModelError;
import com.ogawa.fico.exception.ServerError;

// do not use @Slf4j here, because we want to set the log level and config file before the logger is initialized
public class Main {

    private static final org.slf4j.Logger log;

    static {
        // initialize logging before using it
        Logging.initialize();
        // analog to @Slf4j annotation
        log = org.slf4j.LoggerFactory.getLogger(Main.class);
    }

    private static void logError(ApplicationError applicationError) {

        String message = applicationError.getMessage() == null ? "" : applicationError.getMessage();
        String errorType;

        if (applicationError instanceof CommandLineError) {
            errorType = "Command Line Error";
        } else if (applicationError instanceof ExecutionError) {
            errorType = "Execution Error";
        } else if (applicationError instanceof ModelError) {
            errorType = "Model Error";
        } else if (applicationError instanceof ServerError) {
            errorType = "Server Error";
        } else {
            errorType = "Unknown Error";
        }

        log.error("{}: {}", errorType, message);
    }

    public static void main(String[] args) {

        try {

            Command command = CommandLineParser.parse(args);
            command.run();

        } catch (ExecutionError | ModelError | CommandLineError | ServerError knownException) {
            logError(knownException);
            System.exit(knownException.getErrorCode().getCode());

        } catch (Exception unexpectedException) {
            String message = unexpectedException.getMessage() == null ? "" : unexpectedException.getMessage();
            log.error(message, unexpectedException);
            System.exit(ErrorCode.UNKNOWN_ERROR.getCode());
        }

        // success
        System.exit(0);

    }

}
