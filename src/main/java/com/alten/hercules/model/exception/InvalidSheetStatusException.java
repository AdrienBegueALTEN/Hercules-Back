package com.alten.hercules.model.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception model if the status of the sheet doesn't allow an action.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class InvalidSheetStatusException extends ResponseEntityException {
	public InvalidSheetStatusException() {
		super("The current mission sheet status doesn't allow this operation.", HttpStatus.FORBIDDEN);
	}
}
