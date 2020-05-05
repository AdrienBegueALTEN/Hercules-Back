package com.alten.hercules.model.exception;

import org.springframework.http.HttpStatus;

public class AlreadyExistingVersionException extends ResponseEntityException {
	public AlreadyExistingVersionException() {
		super("Today's version already exists", HttpStatus.CONFLICT);
	}
}
