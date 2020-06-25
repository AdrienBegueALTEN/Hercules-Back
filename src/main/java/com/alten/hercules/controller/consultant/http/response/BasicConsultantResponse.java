package com.alten.hercules.controller.consultant.http.response;

import java.time.LocalDate;

import com.alten.hercules.model.consultant.Consultant;

/**
 * Class that contains the information for the response of a request that asks for the basic details of a consultant.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class BasicConsultantResponse {
	
	/**
	 * ID of the consultant
	 */
	private Long id;
	
	/**
	 * Email of the consultant
	 */
	private String email;
	
	/**
	 * First name of the consultant
	 */
	private String firstname;
	
	/**
	 * Last name of the consultant
	 */
	private String lastname;
	
	/**
	 * ID of the manager of the consultant
	 */
	private Long manager;
	
	/**
	 * Date of the release of the consultant
	 */
	private LocalDate releaseDate;
	
	public BasicConsultantResponse(Consultant consultant) {
		this.id = consultant.getId();
		this.email = consultant.getEmail();
		this.firstname = consultant.getFirstname();
		this.lastname = consultant.getLastname();
		this.manager = consultant.getManager().getId();
		this.releaseDate = consultant.getReleaseDate();
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

	public LocalDate getReleaseDate() {return releaseDate;}
	public void setReleaseDate(LocalDate releaseDate) {this.releaseDate = releaseDate;}
	
	

}
