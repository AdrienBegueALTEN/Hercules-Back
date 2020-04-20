package com.alten.hercules.model.diploma;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class DiplomaLocation {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String city;

	public DiplomaLocation(String city) {
		super();
		this.city = city;
	}

	public DiplomaLocation(Long id, String city) {
		super();
		this.id = id;
		this.city = city;
	}

	public DiplomaLocation() {
		super();
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
