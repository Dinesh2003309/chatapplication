package com.dinesh.userservice.errorhandler;

public class ValidationException extends RuntimeException{
	private final String message;

	public ValidationException(String message) {
		super(message);
		this.message = message;
	}

}