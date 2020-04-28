package com.alten.hercules.model.mission.request;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import com.alten.hercules.model.mission.ESheetStatus;
import com.alten.hercules.model.mission.EType;

public class UpdateMissionRequest {
	
	@NotNull 
	private Long reference;
	
	private String title;
	
	@Length(max = 1000)
	private String description;
	
	private EType type;
	
	private String city;
	
	private String country;
	
	@Length(max = 255)
	private String comment;
	
	private String consultantRole;
	
	private int consultantExperience;
	
	private ESheetStatus state;
	
	private int teamSize;

	public UpdateMissionRequest() {}
	
	public Long getReference() { return reference; }
	public void setReference(Long reference) { this.reference = reference; }

	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }

	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }

	public EType getType() { return type; }
	public void setType(EType type) { this.type = type; }

	public String getCity() { return city; }
	public void setCity(String city) { this.city = city; }

	public String getCountry() { return country; }
	public void setCountry(String country) { this.country = country; }

	public String getComment() { return comment; }
	public void setComment(String comment) { this.comment = comment; }

	public String getConsultantRole() { return consultantRole; }
	public void setConsultantRole(String consultantRole) { this.consultantRole = consultantRole; }

	public Integer getConsultantExperience() { return consultantExperience; }
	public void setConsultantExperience(int consultantExperience) { this.consultantExperience = consultantExperience; }

	public ESheetStatus getState() { return state; }
	public void setState(ESheetStatus state) { this.state = state; }

	public Integer getTeamSize() { return teamSize; }
	public void setTeamSize(int teamSize) { this.teamSize = teamSize; }
}
