package com.mokhir.dev.BookShop.exceptions;

public class ApiControllerException extends Exception{
    public ApiControllerException(String message) {
        super(message);
    }

    public ApiControllerException(Throwable cause) {
        super(cause);
    }
}
