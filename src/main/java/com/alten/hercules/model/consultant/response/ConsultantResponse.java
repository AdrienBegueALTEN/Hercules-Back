package com.alten.hercules.model.consultant.response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.alten.hercules.model.consultant.Consultant;

public class ConsultantResponse {
	
	private Long id;

	private String email;
	
	private String firstname;
	
	private String lastname;
	
	private int experience;
	
	private LocalDate releaseDate;
	
	private Long manager;
	
	private List<Long> diplomas;
	
	public ConsultantResponse(Consultant consultant) {
		this.id = consultant.getId();
		this.email = consultant.getEmail();
		this.firstname = consultant.getFirstname();
		this.lastname = consultant.getLastname();
		this.experience = consultant.getExperience();
		this.releaseDate = consultant.getReleaseDate();
		this.manager = consultant.getManager().getId();
		this.diplomas = new ArrayList<>();
		consultant.getDiplomas().forEach((diploma) -> { this.diplomas.add(diploma.getId());});
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public String getFirstname() { return firstname; }
	public void setFirstname(String firstname) { this.firstname = firstname; }

	public String getLastname() { return lastname; }
	public void setLastname(String lastname) { this.lastname = lastname; }

	public int getExperience() { return experience; }
	public void setExperience(int experience) { this.experience = experience; }

	public LocalDate getReleaseDate() { return releaseDate; }
	public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

	public Long getManager() { return manager; }
	public void setManager(Long manager) { this.manager = manager; }

	public List<Long> getDiplomas() { return diplomas; }
	public void setDiplomas(List<Long> diplomas) { this.diplomas = diplomas; }



}
