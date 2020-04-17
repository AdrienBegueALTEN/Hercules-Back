package com.alten.hercules.model.request.user;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.alten.hercules.consts.UserConst;

public class RegisterUserRequest {
	
	@NotNull
	@Pattern(regexp = UserConst.EMAIL_PATTERN, message = UserConst.EMAIL_PATTERN_MSG)
	private String email;
	
	@NotNull
	private String password;
	
	@NotNull
	private String firstname;
	
	@NotNull
	private String lastname;
	
	@NotNull
	@Pattern(
			regexp = "ADMIN|MANAGER|RECRUITEMENT_OFFICER", 
			message = "Le rôle doit être soit 'ADMIN', soit 'MANAGER', soit 'RECRUITEMENT_OFFICER'")
	private String role;

	public RegisterUserRequest(String email, String password, String firstname, String lastname, String role) {
		this.email = email;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.role = role;
	}
	
	public void setEmail(String email) { this.email = email; }
	public String getEmail() { return this.email; }
	
	public void setPassword(String password) { this.email = password; }
	public String getPassword() { return password; }
	
	public String getFirstname() { return firstname; }
	public void setFirstname(String firstname) { this.firstname = firstname; }

	public String getLastname() { return lastname; }
	public void setLastname(String lastname) { this.lastname = lastname; }
	
	public String getRole() { return role; }
	public void setRole(String role) { this.role = role; }

}
