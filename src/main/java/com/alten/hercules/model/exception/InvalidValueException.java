package com.alten.hercules.model.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception model if a given value is invalid.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class InvalidValueException extends ResponseEntityException {
	public InvalidValueException() {
		super("Invalid value.", HttpStatus.BAD_REQUEST);
	}
}
