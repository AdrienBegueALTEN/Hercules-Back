package com.alten.hercules.model.skill;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.alten.hercules.model.project.Project;

@Entity
public class Skill {
	@Id
	private String label;
	
	@ManyToMany(mappedBy = "skills")
	private Set<Project> projects = new HashSet<>();

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

	public Set<Project> getProjects() {
		return projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}
	
	
	
	
}