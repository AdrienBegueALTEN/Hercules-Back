package com.alten.hercules.controller.authentication.http.request;

import javax.validation.constraints.NotBlank;

/**
 * Class that contains the information for a request to log in.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class LoginRequest {
	
	/**
	 * Email given for the login
	 */
	@NotBlank
	private String email;
	
	/**
	 * Password given for the login
	 */
	@NotBlank
	private String password;
	
	public LoginRequest(String email, String password) {
		this.email = email;
		this.password = password;
	}
	
	public void setEmail(String email) { this.email = email; }
	public String getEmail() { return this.email; }
	
	public void setPassword(String password) { this.email = password; }
	public String getPassword() { return password;}

}
