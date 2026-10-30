package com.demo.fintrack.Service.Exception;

public class CategoryNotBelongToUserException extends RuntimeException{
    public CategoryNotBelongToUserException(String message) {
        super(message);
    }
}
