package com.dinesh.userservice.errorhandler;

public class UnhandlerException  extends RuntimeException {
	private String message;

	public UnhandlerException(String message) {
		super(message);
		this.message = message;
	}

}
