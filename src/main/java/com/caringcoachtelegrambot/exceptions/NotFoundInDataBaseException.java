package com.caringcoachtelegrambot.exceptions;

public class NotFoundInDataBaseException extends RuntimeException {

    public NotFoundInDataBaseException() {
        super();
    }

    public NotFoundInDataBaseException(String msg) {
        super(msg);
    }

    public NotFoundInDataBaseException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
