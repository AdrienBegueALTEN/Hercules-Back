package com.alten.hercules.model.exception;

public class InvalidValueException extends RuntimeException {
	public InvalidValueException(String ressource) {
		super("Invalid value for '" + ressource + "'.");
	}
}
