package com.alten.hercules.model.exception;

import org.springframework.http.HttpStatus;

public class NotLastVersionException extends ResponseEntityException {
	public NotLastVersionException() {
		super("This operation can be done only for the last version of a mission sheet.", HttpStatus.FORBIDDEN);
	}
}
