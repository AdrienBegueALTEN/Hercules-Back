package com.alten.hercules.model.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception model if the version of the mission is not the last one.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class NotLastVersionException extends ResponseEntityException {
	public NotLastVersionException() {
		super("This operation can be done only for the last version of a mission sheet.", HttpStatus.FORBIDDEN);
	}
}
