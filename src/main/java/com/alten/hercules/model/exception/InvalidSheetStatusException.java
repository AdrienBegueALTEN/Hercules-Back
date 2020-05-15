package com.alten.hercules.model.exception;

import org.springframework.http.HttpStatus;

public class InvalidSheetStatusException extends ResponseEntityException {
	public InvalidSheetStatusException() {
		super("The current mission sheet status doesn't allow this operation.", HttpStatus.CONFLICT);
	}
}
