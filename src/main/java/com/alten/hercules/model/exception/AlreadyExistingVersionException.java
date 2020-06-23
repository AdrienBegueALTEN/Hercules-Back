package com.alten.hercules.model.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception model if a version was already made today.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class AlreadyExistingVersionException extends ResponseEntityException {
	public AlreadyExistingVersionException() {
		super("Today's version already exists", HttpStatus.CONFLICT);
	}
}
