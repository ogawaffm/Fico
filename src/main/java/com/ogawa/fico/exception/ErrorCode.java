package com.ogawa.fico.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // ***** Command Line errors *****

    MISSING_COMMAND(1, "Missing command", "No command was given"),

    UNKNOWN_COMMAND(2, "Unknown command", "Command not recognized"),

    INVALID_NUMBER_OF_COMMAND_ARGUMENTS(3, "Invalid number of command arguments",
        "The number of arguments is invalid (too few or too many)"),

    INVALID_ARGUMENTS(3, "Invalid command argument", "The argument is invalid"),

    // ***** Database errors *****

    DATABASE_ARGUMENT_NOT_ALLOWED(10, "Database argument not allowed",
        "The command does not allow a database argument (@DatabaseName)"),

    DATABASE_CONNECTION_ERROR(11, "Database connection error",
        "Could not connect to the database"),

    DATABASE_PORT_ERROR(12, "Database port error",
        "Invalid port number for the database connection"),

    DATABASE_MODEL_ERROR(13, "Database model error",
        "The database model is invalid (e.g. wrong FiCo version)"),

    DATABASE_PROCESSING_ERROR(14, "Database processing error",
        "An error occurred while processing in the database"),

    // ***** File I/O errors *****

    FILE_NOT_FOUND_ERROR(20, "File not found error", "The file does not exist"),

    ACCESS_DENIED_ERROR(21, "Access denied error",
        "The access to the file or directory is denied because of insufficient permissions"),

    FILE_ALREADY_EXISTS_ERROR(22, "File already exists error",
        "Operation expected a non-existing file but found an existing file"),

    NOT_DIRECTORY_ERROR(23, "Not a directory error",
        "An operation expected a directory but could not find a directory"),

    DIRECTORY_NOT_EMPTY_ERROR(24, "Directory not empty error",
        "An operation expected an empty directory but found a non-empty directory"),

    FILE_SYSTEM_LOOP_ERROR(25, "File system loop error",
        "An operation expected a file system without loops (e.g. circular symlinks) but found a loop"),

    ENCODING_ERROR(40, "Encoding error",
        "The encoding of the file is not supported or invalid"),

    // ***** Network errors *****

    NETWORK_ERROR(50, "Network socket error",
        "An error occurred while communicating over the network (e.g. address not reachable, connection refused, timeout etc.)"),

    SERVER_ERROR(80, "Database server error", "Database server error"),

    /**
     * The error code for an unknown error. 255 is the maximum value for %ERRORLEVEL% in Windows.
     */
    UNKNOWN_ERROR(255, "Unknown error", "An unknown (unexpectable) error occurred");

    private final int code;

    private final String name;
    private final String description;

    ErrorCode(int code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

}
