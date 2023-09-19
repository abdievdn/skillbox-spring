package com.example.MyBookShopApp.errors;

public class EntityNotFoundError extends Exception {

    public EntityNotFoundError(String message) {
        super(message);
    }
}
