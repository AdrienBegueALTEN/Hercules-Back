package com.alten.hercules.model.diploma;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "id"})
public class DiplomaLocation {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String city;
	private String school;

	public DiplomaLocation(String city, String school) {
		super();
		this.city = city;
		this.school = school;
	}

	public DiplomaLocation(Long id, String city, String school) {
		super();
		this.id = id;
		this.school = school;
		this.city = city;
	}

	public DiplomaLocation() {
		super();
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
	
	
	
	
	
}