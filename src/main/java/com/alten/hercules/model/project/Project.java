package com.alten.hercules.model.project;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.alten.hercules.model.mission.MissionSheet;
import com.alten.hercules.model.skill.Skill;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Class model for a project.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
@Entity
public class Project {
	
	/**
	 * ID of the project
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id = (long) 0 ;
	
	/**
	 * Mission's sheet of the project
	 */
	@JsonIgnore
	@ManyToOne
	private MissionSheet missionSheet;
	
	/**
	 * Title of the project
	 */
	@Column(nullable = false, columnDefinition = "VARCHAR(100) default ''")
	private String title = "";
	
	/**
	 * A description about the project
	 */
	@Column(nullable = false, columnDefinition = "VARCHAR(1000) default ''")
	private String description = "";
	
	/**
	 * Date of beginning of the project
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Column(nullable = true)
	private LocalDate beginDate = null;
	
	/**
	 * Date of end of the project
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Column(nullable = true)
	private LocalDate endDate = null;
	
	/**
	 * A path for the picture of the project
	 */
	@Column(nullable = true)
	private String picture = null;
	
	/**
	 * Set of skills of the project
	 */
	@ManyToMany
	@JoinTable(
			  name = "projects_skills", 
			  joinColumns = @JoinColumn(name = "project_id"), 
			  inverseJoinColumns = @JoinColumn(name = "skill_label"))
	private Set<Skill> skills = new HashSet<>();
	
	/**
	 * Empty constructor
	 */
	public Project() {}
	
	public Project(MissionSheet missionSheet) {
		this.missionSheet = missionSheet;
	}
	
	/**
	 * Constructor
	 */
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

	public LocalDate getBeginDate() { return beginDate; }
	public void setBeginDate(LocalDate beginDate) { this.beginDate = beginDate; }

	public LocalDate getEndDate() { return endDate; }
	public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

	public String getPicture() { return picture; }
	public void setPicture(String picture) { this.picture = picture; }
	
	public Set<Skill> getSkills() { return skills; }
	public void setSkills(Set<Skill> skills) { this.skills = skills; }

	public MissionSheet getMissionSheet() { return missionSheet; }
	
	@Override
	public int hashCode() {
		return this.id.intValue();
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (obj == null) return false;
	    if (!(obj instanceof Project))
	        return false;
	    if (obj == this)
	        return true;
	    return this.getId() == ((Project) obj).getId();
	}
	
}
