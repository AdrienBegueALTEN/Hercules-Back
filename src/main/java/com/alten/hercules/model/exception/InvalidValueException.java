package com.alten.hercules.model.exception;

import org.springframework.http.HttpStatus;

public class InvalidValueException extends ResponseEntityException {
	public InvalidValueException() {
		super("Invalid value.", HttpStatus.BAD_REQUEST);
	}
}
