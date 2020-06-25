package com.alten.hercules.controller.user.http.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.alten.hercules.consts.AppConst;
import com.alten.hercules.model.exception.InvalidValueException;
import com.alten.hercules.model.user.AppUser;

public abstract class AddUserRequest {
	
	/**
	 * Email of the user
	 */
	@NotNull
	@Pattern(regexp = AppConst.EMAIL_PATTERN)
	protected String email;
	
	/**
	 * First name of the user
	 */
	@NotNull protected String firstname;
	
	/**
	 * Last name of the user
	 */
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
	
	/**
	 * Function that generates an user with the fields.
	 * @return An AppUser
	 * @throws InvalidValueException
	 */
	public abstract AppUser buildUser() throws InvalidValueException;
	
}
