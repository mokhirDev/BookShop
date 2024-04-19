package com.mokhir.dev.BookShop.service;

public class IncorrectValueException extends Exception{
    public IncorrectValueException(String message) {
        super(message);
    }

    public IncorrectValueException(Throwable cause) {
        super(cause);
    }
}
