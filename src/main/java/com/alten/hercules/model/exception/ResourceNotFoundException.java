package com.alten.hercules.model.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ResponseEntityException {
	public ResourceNotFoundException(Class<?> resourceClass) {
		super(resourceClass.getName() + " not found.", HttpStatus.NOT_FOUND);
	}
}
