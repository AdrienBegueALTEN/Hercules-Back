package com.alten.hercules.model.exception;

import org.springframework.http.HttpStatus;

public class UnvalidatedMissionSheetException extends ResponseEntityException {
	public UnvalidatedMissionSheetException() {
		super("The mission sheet must be validated to do this operation.", HttpStatus.CONFLICT);
	}
}
