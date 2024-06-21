package com.chatservice.errorhandler;


public class UnhandlerException  extends RuntimeException {
    private final String message;

    public UnhandlerException(String message) {
        super(message);
        this.message = message;
    }

}
