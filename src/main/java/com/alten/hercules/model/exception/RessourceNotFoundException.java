package com.alten.hercules.model.exception;

import org.springframework.http.HttpStatus;

public class RessourceNotFoundException extends ResponseEntityException {
	public RessourceNotFoundException(String ressource) {
		super("Ressource '" + ressource + "' not found.", HttpStatus.NOT_FOUND);
	}
}
