package com.alten.hercules.controller.consultant.http.response;

import com.alten.hercules.model.consultant.Consultant;

/**
 * Class that contains the information for the response of a request that asks for the basic details of a consultant.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class BasicConsultantResponse {
	
	private Long id;

	private String email;
	
	private String firstname;
	
	private String lastname;
	
	private Long manager;
	
	public BasicConsultantResponse(Consultant consultant) {
		this.id = consultant.getId();
		this.email = consultant.getEmail();
		this.firstname = consultant.getFirstname();
		this.lastname = consultant.getLastname();
		this.manager = consultant.getManager().getId();
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public String getFirstname() { return firstname; }
	public void setFirstname(String firstname) { this.firstname = firstname; }

	public String getLastname() { return lastname; }
	public void setLastname(String lastname) { this.lastname = lastname; }

	public Long getManager() { return manager; }
	public void setManager(Long manager) { this.manager = manager; }

}
