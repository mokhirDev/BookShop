package com.mokhir.dev.BookShop.exceptions;

public class EntityNotLeftException extends Exception {
    public EntityNotLeftException(String message) {
        super(message);
    }

    public EntityNotLeftException(Throwable cause) {
        super(cause);
    }
}
