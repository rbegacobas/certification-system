package com.certificationapp.certification_system.exception;

public class InvalidFileTypeException extends FileStorageException {
    public InvalidFileTypeException(String message) {
        super(message);
    }
}
