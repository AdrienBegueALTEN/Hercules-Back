package com.alten.hercules.model.request;

import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class RegisterRequest {
	
	@NotBlank
	@Size(max = 64)
	@Email
	private String email;
	
	@NotBlank
	private String password;
	
	@Size(min = 2, max = 32)
	private String firstname;
	
	@Size(min = 2, max = 32)
	private String lastname;
	
	private Set<String> roles;

	public RegisterRequest(String email, String password, String firstname, String lastname, Set<String> roles) {
		this.email = email;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.roles = roles;
	}
	
	public void setEmail(String email) { this.email = email; }
	public String getEmail() { return this.email; }
	
	public void setPassword(String password) { this.email = password; }
	public String getPassword() { return password; }
	
	public String getFirstname() { return firstname; }
	public void setFirstname(String firstname) { this.firstname = firstname; }

	public String getLastname() { return lastname; }
	public void setLastname(String lastname) { this.lastname = lastname; }
	
	public Set<String> getRoles() { return roles; }
	public void setRoles(Set<String> roles) { this.roles = roles; }

}
