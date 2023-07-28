package com.caringcoachtelegrambot.exceptions;

public class FileProcessingException extends RuntimeException {
    public FileProcessingException(String message) {
        super(message);
    }

    public FileProcessingException() {
        super();
    }

    public FileProcessingException(String msg, Throwable cause) {
        super(msg, cause);
    }
}