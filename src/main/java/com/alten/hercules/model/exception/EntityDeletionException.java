package com.alten.hercules.model.exception;

import org.springframework.http.HttpStatus;

public class EntityDeletionException extends ResponseEntityException {
	public EntityDeletionException(String message) {
		super(message, HttpStatus.CONFLICT);
	}
}
