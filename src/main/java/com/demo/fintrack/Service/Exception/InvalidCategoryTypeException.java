package com.demo.fintrack.Service.Exception;

public class InvalidCategoryTypeException extends RuntimeException{
    public InvalidCategoryTypeException(String message) {
        super(message);
    }
}
