package com.alten.hercules.controller.user.http.request.manager;

import com.alten.hercules.controller.user.http.request.AddUserRequest;
import com.alten.hercules.model.exception.InvalidValueException;
import com.alten.hercules.model.user.Manager;

/**
 * Class that contains the information for a request that adds a manager
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class AddManagerRequest extends AddUserRequest {
	
	/**
	 * Boolean that indicates if the manager has the administration rights.
	 */
	private boolean isAdmin;
	
	public AddManagerRequest(String email, String firstname, String lastname, boolean isAdmin) {
		super(email, firstname, lastname);
		this.isAdmin = isAdmin;
	}
	
	public boolean isAdmin() { return isAdmin; }
	public void setAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }
	
	@Override
	public Manager buildUser() throws InvalidValueException {
		return new Manager(email, firstname, lastname, isAdmin);
	}

}
