package com.chatservice.errorhandler;

public class ValidationException extends RuntimeException{
    private String message;

    public ValidationException(String message) {
        super(message);
        this.message = message;
    }

}
