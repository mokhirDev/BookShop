package com.mokhir.dev.BookShop.exceptions;

public class NoCreatedEntityYetException extends RuntimeException{

    public NoCreatedEntityYetException(String message){
        super(message);
    }
}
