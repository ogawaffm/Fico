package com.ogawa.fico.exception;

import java.io.FileNotFoundException;
import java.net.SocketException;
import java.nio.charset.CharacterCodingException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NotDirectoryException;
import java.nio.file.FileSystemLoopException;
import java.nio.file.NoSuchFileException;

/**
 * Marker class for exceptions thrown for expectable errors.
 */
public class ExecutionError extends ApplicationError {

    public ExecutionError(String message) {
        super(message);
        setErrorCode(ErrorCode.UNKNOWN_ERROR);
    }

    public ExecutionError(String message, Throwable cause) {
        super(message, cause);

        if (cause instanceof SocketException) {
            setErrorCode(ErrorCode.NETWORK_ERROR);

        } else if (cause instanceof FileNotFoundException || cause instanceof NoSuchFileException) {
            setErrorCode(ErrorCode.FILE_NOT_FOUND_ERROR);

        } else if (cause instanceof AccessDeniedException) {
            setErrorCode(ErrorCode.ACCESS_DENIED_ERROR);

        } else if (cause instanceof FileAlreadyExistsException) {
            setErrorCode(ErrorCode.FILE_ALREADY_EXISTS_ERROR);

        } else if (cause instanceof NotDirectoryException) {
            setErrorCode(ErrorCode.NOT_DIRECTORY_ERROR);

        } else if (cause instanceof DirectoryNotEmptyException) {
            setErrorCode(ErrorCode.FILE_SYSTEM_LOOP_ERROR);

        } else if (cause instanceof NotDirectoryException) {
            setErrorCode(ErrorCode.NOT_DIRECTORY_ERROR);

        } else if (cause instanceof FileSystemLoopException) {
            setErrorCode(ErrorCode.FILE_SYSTEM_LOOP_ERROR);

        } else if (cause instanceof CharacterCodingException) {
            setErrorCode(ErrorCode.ENCODING_ERROR);

        } else {
            setErrorCode(ErrorCode.UNKNOWN_ERROR);
        }
    }

}
