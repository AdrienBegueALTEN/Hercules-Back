package com.alten.hercules.model.exception;

import java.io.IOException;

public class InvalidFieldNameException extends IOException {
	public InvalidFieldNameException() {
		super("Invalid field name.");
	}
}
