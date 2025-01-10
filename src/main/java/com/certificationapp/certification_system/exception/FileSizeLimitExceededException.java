package com.certificationapp.certification_system.exception;

public class FileSizeLimitExceededException extends FileStorageException {
    public FileSizeLimitExceededException(String message) {
        super(message);
    }
}
