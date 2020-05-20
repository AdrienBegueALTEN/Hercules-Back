package com.alten.hercules.model.exception;

import org.springframework.http.HttpStatus;

public class ProjectsBoundsException extends ResponseEntityException {
	public ProjectsBoundsException() {
		super("A mission must contain 1 to 5 projects.", HttpStatus.FORBIDDEN);
	}
}
