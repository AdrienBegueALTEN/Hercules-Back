package com.alten.hercules.model.exception;

public class AlreadyExistingVersionException extends RuntimeException {
	public AlreadyExistingVersionException() {
		super("Today's version already exists");
	}
}
