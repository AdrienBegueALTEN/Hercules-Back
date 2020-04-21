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
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "id"})
public class DiplomaName {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String name;
	
	@OneToOne(fetch = FetchType.LAZY)
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}
	
	
	
	
	
	
}