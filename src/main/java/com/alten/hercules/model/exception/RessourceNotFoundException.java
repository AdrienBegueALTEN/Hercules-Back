package com.alten.hercules.model.exception;

import java.io.IOException;

public class RessourceNotFoundException extends IOException {
	public RessourceNotFoundException(String ressource) {
		super("Ressource '" + ressource + "' not found.");
	}
}
