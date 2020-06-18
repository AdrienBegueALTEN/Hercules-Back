package com.alten.hercules.controller.consultant.http.response;

import java.time.LocalDate;
import java.util.Set;
import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.diploma.Diploma;
import com.alten.hercules.model.user.Manager;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Class that contains the information for the response of a request that asks for the details of a consultant.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ConsultantResponse {
	private Long id;
	private String email;
	private String firstname;
	private String lastname;
	private int experience;
	private LocalDate releaseDate;
	@JsonIgnoreProperties(value = {"consultants", "email"})
	private Manager manager;
	private Set<Diploma> diplomas;
	
	public ConsultantResponse(Consultant consultant) {
		this.id = consultant.getId();
		this.email = consultant.getEmail();
		this.firstname = consultant.getFirstname();
		this.lastname = consultant.getLastname();
		this.experience = consultant.getExperience();
		this.releaseDate = consultant.getReleaseDate();
		this.manager = consultant.getManager();
		this.diplomas = consultant.getDiplomas();
	}
	
	public Long getId() { return id; }
	public String getEmail() { return email; }
	public String getFirstname() { return firstname; }
	public String getLastname() { return lastname; }
	public int getExperience() { return experience; }
	public LocalDate getReleaseDate() { return releaseDate; }
	public Manager getManager() { return manager; }
	public Set<Diploma> getDiplomas() { return diplomas; }
}
