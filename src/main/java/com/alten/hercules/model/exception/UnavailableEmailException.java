package com.alten.hercules.model.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception class if the email is already used.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class UnavailableEmailException extends ResponseEntityException {
	public UnavailableEmailException() {
		super("Unavailable email adress.", HttpStatus.CONFLICT);
	}
}
