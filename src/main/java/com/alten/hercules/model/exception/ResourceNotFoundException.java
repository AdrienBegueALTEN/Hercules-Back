package com.alten.hercules.model.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ResponseEntityException {
	public ResourceNotFoundException(String ressource) {
		super("Ressource '" + ressource + "' not found.", HttpStatus.NOT_FOUND);
	}
}
