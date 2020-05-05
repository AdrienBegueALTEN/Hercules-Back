package com.alten.hercules.model.exception;

import org.springframework.http.HttpStatus;

public class InvalidFieldnameException extends ResponseEntityException {
	public InvalidFieldnameException() {
		super("Invalid field name.", HttpStatus.BAD_REQUEST);
	}
}
