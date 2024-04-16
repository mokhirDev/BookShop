package com.mokhir.dev.BookShop.exceptions;

public class CurrentUserNotOwnCurrentEntityException extends Exception{
    public CurrentUserNotOwnCurrentEntityException(String message) {
        super(message);
    }

    public CurrentUserNotOwnCurrentEntityException(Throwable cause) {
        super(cause);
    }
}
