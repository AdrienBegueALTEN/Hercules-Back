package com.alten.hercules.model.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ResponseEntityException {
	public ResourceNotFoundException(Class<?> resource) {
		super(resource.getName() + " not found.", HttpStatus.NOT_FOUND);
	}
}
