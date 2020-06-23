package com.alten.hercules.model.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception model if a given field's name doesn't exist.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class InvalidFieldnameException extends ResponseEntityException {
	public InvalidFieldnameException() {
		super("Invalid field name.", HttpStatus.BAD_REQUEST);
	}
}
