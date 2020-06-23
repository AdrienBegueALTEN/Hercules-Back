package com.alten.hercules.model.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception model if a query can't find an object in the database.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class ResourceNotFoundException extends ResponseEntityException {
	public ResourceNotFoundException(Class<?> resourceClass) {
		super(resourceClass.getName() + " not found.", HttpStatus.NOT_FOUND);
	}
}
