package com.alten.hercules.controller.consultant.http.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.alten.hercules.consts.AppConst;

/**
 * Class that contains the information for a request that adds a consultant.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class AddConsultantRequest {
	
	@NotNull
	@Pattern(regexp = AppConst.EMAIL_PATTERN)
	private String email;

	@NotNull
	private String firstname;
	
	@NotNull
	private String lastname;
	
	@NotNull
	private Long manager;
	
	public AddConsultantRequest(String email, String firstname, String lastname, Long manager) {
		this.email = email;
		this.firstname = firstname;
		this.lastname = lastname;
		this.manager = manager;
	}
	
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public String getFirstname() { return firstname; }
	public void setFirstname(String firstname) { this.firstname = firstname; }
	
	public String getLastname() { return lastname; }
	public void setLastname(String lastname) { this.lastname = lastname; }

	public Long getManager() { return manager; }
	public void setManager(Long idManager) { this.manager = idManager; }

}
