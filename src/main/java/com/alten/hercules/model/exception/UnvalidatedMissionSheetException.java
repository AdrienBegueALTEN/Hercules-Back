package com.alten.hercules.model.exception;

public class UnvalidatedMissionSheetException extends RuntimeException {
	public UnvalidatedMissionSheetException() {
		super("The mission sheet must be validated to do this operation.");
	}
}
