package com.mokhir.dev.BookShop.exceptions;

public class RoleNotFoundException extends Exception{
    public RoleNotFoundException(String message) {
        super(message);
    }

    public RoleNotFoundException(Throwable cause) {
        super(cause);
    }
}
