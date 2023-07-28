package com.caringcoachtelegrambot.exceptions;

public class NotReturnedResponseException extends RuntimeException {

    public NotReturnedResponseException() {
        super("Ответ не был сформирован");
    }

    public NotReturnedResponseException(String msg) {
        super(msg);
    }

    public NotReturnedResponseException(String msg, Throwable cause) {
        super(msg, cause);
    }
}