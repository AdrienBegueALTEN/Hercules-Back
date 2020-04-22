package com.alten.hercules.model.mission.request;

import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.mission.EState;
import com.alten.hercules.model.mission.EType;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class MissionRequest {
	
	private Long id;
	
	private Long reference;
	
	private Date lastUpdate;
	
	private String title;
	
	@Length(max = 1000)
	private String description;
	
	private EType type;
	
	private String city;
	
	private String country;
	
	@Length(max = 250)
	private String comment;
	
	private String consultantRole;
	
	private int consultantExperience;
	
	private EState state;
	
	private int teamSize;
	
	private Long consultantId;
	
	private Long customerId;
	

	public MissionRequest() {
		super();
	}
	
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	

	public Long getReference() {
		return reference;
	}



	public void setReference(Long reference) {
		this.reference = reference;
	}



	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public EType getType() {
		return type;
	}

	public void setType(EType type) {
		this.type = type;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getConsultantRole() {
		return consultantRole;
	}

	public void setConsultantRole(String consultantRole) {
		this.consultantRole = consultantRole;
	}

	public Integer getConsultantExperience() {
		return consultantExperience;
	}

	public void setConsultantExperience(int consultantExperience) {
		this.consultantExperience = consultantExperience;
	}

	public EState getState() {
		return state;
	}

	public void setState(EState state) {
		this.state = state;
	}

	public Integer getTeamSize() {
		return teamSize;
	}

	public void setTeamSize(int teamSize) {
		this.teamSize = teamSize;
	}

	public Long getConsultantId() {
		return consultantId;
	}

	public void setConsultantId(Long consultantId) {
		this.consultantId = consultantId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	
	
	
	
}
