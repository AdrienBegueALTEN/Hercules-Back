package com.alten.hercules.model.exception;

public class InvalidRessourceFormatException extends RuntimeException {
	public InvalidRessourceFormatException(String ressource) {
		super("Invalid '" + ressource + "' format.");
	}
}
