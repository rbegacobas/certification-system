package com.certificationapp.certification_system.exception;

/**
 * Exception thrown for file storage related errors.
 * Used to handle errors during file upload, storage, and deletion operations.
 */
public class FileStorageException extends RuntimeException {

    /**
     * Constructs a new file storage exception with the specified detail message.
     *
     * @param message the detail message
     */
    public FileStorageException(String message) {
        super(message);
    }

    /**
     * Constructs a new file storage exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}