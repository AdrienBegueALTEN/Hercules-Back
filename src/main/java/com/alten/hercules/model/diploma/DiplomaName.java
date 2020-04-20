package com.alten.hercules.model.diploma;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class DiplomaName {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String name;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade = {
            CascadeType.ALL
    })
	@JoinColumn(name = "level_id", nullable = false)
	private Level level;

	public DiplomaName(String name, Level level) {
		super();
		this.name = name;
		this.level = level;
	}

	public DiplomaName(Long id, String name, Level level) {
		super();
		this.id = id;
		this.name = name;
		this.level = level;
	}

	public DiplomaName() {
		super();
	}
	
	
	
	
	
	
}
