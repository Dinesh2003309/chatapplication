package com.dinesh.userservice.errorhandler;

public class UserNotFoundException extends RuntimeException {
    private final String message;

    public UserNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
