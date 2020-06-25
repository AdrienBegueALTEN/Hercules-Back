package com.alten.hercules.model.skill;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.alten.hercules.model.project.Project;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Class model for a skill.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
@Entity
public class Skill {
	
	/**
	 * Name of the skill
	 */
	@Id
	private String label;
	
	/**
	 * Set of projects that contain the skill
	 */
	@JsonIgnore
	@ManyToMany(mappedBy = "skills")
	private Set<Project> projects = new HashSet<>();
	
	/**
	 * Constructor with the name
	 */
	public Skill(String label) {
		super();
		this.label = label;
	}
	
	/**
	 * Empty constructor
	 */
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