package com.caringcoachtelegrambot.exceptions;

public class NotValidDataException extends RuntimeException {
    public NotValidDataException(String msg) {
        super(msg);
    }

    public NotValidDataException() {
        super("Вы отправили не валидные данные");
    }
    public NotValidDataException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
