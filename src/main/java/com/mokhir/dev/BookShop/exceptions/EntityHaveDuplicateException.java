package com.mokhir.dev.BookShop.exceptions;

public class EntityHaveDuplicateException extends Exception{
    public EntityHaveDuplicateException(String message) {
        super(message);
    }

    public EntityHaveDuplicateException(Throwable cause) {
        super(cause);
    }
}
