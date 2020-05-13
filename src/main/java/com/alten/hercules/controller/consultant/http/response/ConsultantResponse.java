package com.alten.hercules.controller.consultant.http.response;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.user.Manager;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ConsultantResponse {

	private Long id;
	private String email;
	private String firstname;
	private String lastname;
	private int experience;
	private Date releaseDate;
	@JsonIgnoreProperties(value = {"consultants", "email"})
	private Manager manager;
	private List<Map<String, Object>> diplomas;
	
	public ConsultantResponse(Consultant consultant) {
		this.id = consultant.getId();
		this.email = consultant.getEmail();
		this.firstname = consultant.getFirstname();
		this.lastname = consultant.getLastname();
		this.experience = consultant.getExperience();
		this.releaseDate = consultant.getReleaseDate();
		this.manager = consultant.getManager();
		this.diplomas = consultant.getDiplomas().stream()
				.map(diploma -> {
					Map<String, Object> mappedDiploma = new HashMap<String, Object>();
					mappedDiploma.put("id", diploma.getId());
					mappedDiploma.put("city", diploma.getDiplomaLocation().getCity());
					mappedDiploma.put("establishment", diploma.getDiplomaLocation().getSchool());
					mappedDiploma.put("entitled", diploma.getDiplomaName().getName());
					mappedDiploma.put("level", diploma.getDiplomaName().getLevel().getName());
					mappedDiploma.put("year", diploma.getGraduationYear());
					return mappedDiploma;
					})
				.collect(Collectors.toList());
	}
	
	public Long getId() { return id; }
	public String getEmail() { return email; }
	public String getFirstname() { return firstname; }
	public String getLastname() { return lastname; }
	public int getExperience() { return experience; }
	public Date getReleaseDate() { return releaseDate; }
	public Manager getManager() { return manager; }
	public List<Map<String, Object>> getDiplomas() { return diplomas; }
}
