package com.alten.hercules.model.skill;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Skill {
	@Id
	private String label;

	public Skill(String label) {
		super();
		this.label = label;
	}

	public Skill() {
		super();
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	
}