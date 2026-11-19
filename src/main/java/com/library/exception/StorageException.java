package com.library.exception;

public class StorageException extends LibraryException {
    public StorageException(String message) {
        super("Could not read data file: " + message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
