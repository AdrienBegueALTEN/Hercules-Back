package com.alten.hercules.model.exception;

import org.springframework.http.HttpStatus;

public class UnavailableEmailException extends ResponseEntityException {
	public UnavailableEmailException() {
		super("Unavailable email adress.", HttpStatus.CONFLICT);
	}
}
