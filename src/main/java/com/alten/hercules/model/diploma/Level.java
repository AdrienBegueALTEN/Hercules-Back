package com.alten.hercules.model.diploma;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Level {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String name;

	public Level(String name) {
		super();
		this.name = name;
	}

	@Override
	public String toString() {
		return "Level [id=" + id + ", name=" + name + "]";
	}

	public Level(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Level() {
		super();
	}
	
	
	
	
}
