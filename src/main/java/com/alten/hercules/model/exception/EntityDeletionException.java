package com.alten.hercules.model.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception model if an object can't be deleted.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class EntityDeletionException extends ResponseEntityException {
	public EntityDeletionException(String message) {
		super(message, HttpStatus.CONFLICT);
	}
}
