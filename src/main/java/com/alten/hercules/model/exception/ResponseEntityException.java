package com.alten.hercules.model.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Exception model if the response couldn't be made.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public abstract class ResponseEntityException extends IOException {
	protected HttpStatus status;

	protected ResponseEntityException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}
	
	public ResponseEntity<String> buildResponse() {
		return ResponseEntity
				.status(status)
				.body(super.getMessage());
	}
}
