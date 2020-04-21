package com.alten.hercules.model.diploma;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
public class DiplomaName {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String name;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@MapsId("id")
	private Level level;

	public DiplomaName(String name, Level level) {
		super();
		this.name = name;
		this.level = level;
	}
	
	
	
	
}
