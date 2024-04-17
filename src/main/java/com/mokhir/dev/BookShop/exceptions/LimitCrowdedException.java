package com.mokhir.dev.BookShop.exceptions;

public class LimitCrowdedException extends Exception{
    public LimitCrowdedException(String message) {
        super(message);
    }

    public LimitCrowdedException(Throwable cause) {
        super(cause);
    }
}
