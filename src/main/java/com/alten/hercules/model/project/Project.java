package com.alten.hercules.model.project;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import com.alten.hercules.model.exception.InvalidValueException;
import com.alten.hercules.model.mission.MissionSheet;
import com.alten.hercules.model.skill.Skill;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Project {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@JsonIgnore
	@ManyToOne
	private MissionSheet missionSheet;

	@Column(nullable = false, columnDefinition = "VARCHAR(100) default ''")
	private String title = "";
	
	@Column(nullable = false, columnDefinition = "VARCHAR(1000) default ''")
	private String description = "";
	
	@Column(nullable = true)
	private Date beginDate = null;
	
	@Column(nullable = true)
	private Date endDate = null;

	@Column(nullable = true)
	private String picture = null;
	
	@ManyToMany
	@JoinTable(
			  name = "projects_skills", 
			  joinColumns = @JoinColumn(name = "project_id"), 
			  inverseJoinColumns = @JoinColumn(name = "skill_label"))
	private Set<Skill> skills = new HashSet<>();
	
	public Project() {}
	
	public Project(MissionSheet missionSheet) {
		this.missionSheet = missionSheet;
	}
	
	public Project(Project project, MissionSheet missionSheet) {
		setTitle(project.getTitle());
		setDescription(project.getDescription());
		setBeginDate(project.getBeginDate());
		setEndDate(project.getEndDate());
		this.missionSheet = missionSheet;
	}
	
	public Long getId() { return id; }

	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }

	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }

	public Date getBeginDate() { return beginDate; }
	public void setBeginDate(Date beginDate) { this.beginDate = beginDate; }

	public Date getEndDate() { return endDate; }
	public void setEndDate(Date endDate) { this.endDate = endDate; }

	public String getPicture() { return picture; }
	public void setPicture(String picture) { this.picture = picture; }
	
	public Set<Skill> getSkills() { return skills; }
	public void setSkills(Set<Skill> skills) { this.skills = skills; }

	public MissionSheet getMissionSheet() { return missionSheet; }
	
}
