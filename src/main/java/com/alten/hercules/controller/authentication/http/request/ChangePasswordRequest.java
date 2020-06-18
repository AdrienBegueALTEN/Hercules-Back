package com.alten.hercules.controller.authentication.http.request;

import javax.validation.constraints.NotBlank;

/**
 * Class that contains the information for a request to change the password
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class ChangePasswordRequest {

	@NotBlank
	private String currentPassword;
	
	@NotBlank
	private String newPassword;
	
	public ChangePasswordRequest(String currentPassword, String newPassword) {
		this.currentPassword = currentPassword;
		this.newPassword = newPassword;
	}
	
	public String getCurrentPassword() { return currentPassword; }
	public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

	public String getNewPassword() { return newPassword; }
	public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

}
