package com.demo.fintrack.Service.Exception;

public class BudgetAlreadyExistException extends RuntimeException{
    public BudgetAlreadyExistException(String message) {
        super(message);
    }
}
