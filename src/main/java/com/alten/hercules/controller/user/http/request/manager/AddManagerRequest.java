package com.alten.hercules.controller.user.http.request.manager;

import com.alten.hercules.controller.user.http.request.AddUserRequest;
import com.alten.hercules.model.exception.InvalidValueException;
import com.alten.hercules.model.user.Manager;

public class AddManagerRequest extends AddUserRequest {

	private boolean isAdmin;
	
	public AddManagerRequest(String email, String password, String firstname, String lastname, boolean isAdmin) {
		super(email, password, firstname, lastname);
		this.isAdmin = isAdmin;
	}
	
	public boolean isAdmin() { return isAdmin; }
	public void setAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }
	
	@Override
	public Manager buildUser() throws InvalidValueException {
		return new Manager(email, firstname, lastname, isAdmin);
	}

}
