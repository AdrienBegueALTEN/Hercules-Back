package com.alten.hercules.controller.user.http.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.alten.hercules.consts.AppConst;
import com.alten.hercules.model.exception.InvalidValueException;
import com.alten.hercules.model.user.AppUser;

public abstract class AddUserRequest {

	@NotNull
	@Pattern(regexp = AppConst.EMAIL_PATTERN)
	protected String email;
	
	@NotNull protected String firstname;
	
	@NotNull protected String lastname;
	
	public AddUserRequest(String email, String firstname, String lastname) {
		this.email = email;
		this.firstname = firstname;
		this.lastname = lastname;
	}
	
	public void setEmail(String email) { this.email = email; }
	public String getEmail() { return this.email; }
	
	public String getFirstname() { return firstname; }
	public void setFirstname(String firstname) { this.firstname = firstname; }

	public String getLastname() { return lastname; }
	public void setLastname(String lastname) { this.lastname = lastname; }
	
	public abstract AppUser buildUser() throws InvalidValueException;
	
}
