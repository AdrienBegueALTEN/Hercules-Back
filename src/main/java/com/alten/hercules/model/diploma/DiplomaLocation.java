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
	
	
}
