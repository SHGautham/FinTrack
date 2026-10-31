package com.demo.fintrack.Service.Exception;

public class UserAlreadyFoundException extends RuntimeException{
    public UserAlreadyFoundException(String message) {
        super(message);
    }
}
