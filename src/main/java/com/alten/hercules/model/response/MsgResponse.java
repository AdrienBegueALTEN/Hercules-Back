package com.alten.hercules.model.response;

/**
 * Class model for a message response.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class MsgResponse {
	
	private String message;
	
	public MsgResponse(String message) {
		this.message = message;
	}

	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }
	
}
