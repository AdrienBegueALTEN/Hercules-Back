package com.alten.hercules.model.exception;

import java.io.IOException;

public class UnavailableEmailException extends IOException {
	public UnavailableEmailException() {
		super("Unavailable email adress.");
	}
}
