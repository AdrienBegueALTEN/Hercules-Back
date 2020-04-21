package com.alten.hercules.model.request.user;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.alten.hercules.consts.UserConst;
import com.alten.hercules.model.user.AppUser;

public abstract class AddUserRequest {

	@NotNull
	@Pattern(regexp = UserConst.EMAIL_PATTERN, message = UserConst.EMAIL_PATTERN_MSG)
	protected String email;
	
	@NotNull
	protected String password;
	
	@NotNull
	protected String firstname;
	
	@NotNull
	protected String lastname;
	
	public AddUserRequest(String email, String password, String firstname, String lastname) {
		this.email = email;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
	}
	
	public void setEmail(String email) { this.email = email; }
	public String getEmail() { return this.email; }
	
	public void setPassword(String password) { this.email = password; }
	public String getPassword() { return password; }
	
	public String getFirstname() { return firstname; }
	public void setFirstname(String firstname) { this.firstname = firstname; }

	public String getLastname() { return lastname; }
	public void setLastname(String lastname) { this.lastname = lastname; }
	
	public abstract AppUser buildUser();
	
}
