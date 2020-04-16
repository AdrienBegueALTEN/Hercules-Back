package com.alten.hercules.model.request;

import javax.validation.constraints.NotBlank;

public class LoginRequest {
	
	@NotBlank
	private String email;
	
	@NotBlank
	private String password;
	
	LoginRequest(String email, String password) {
		this.email = email;
		this.password = password;
	}
	
	public void setEmail(String email) { this.email = email; }
	public String getEmail() { return this.email; }
	
	public void setPassword(String password) { this.email = password; }
	public String getPassword() { return password;}

}
