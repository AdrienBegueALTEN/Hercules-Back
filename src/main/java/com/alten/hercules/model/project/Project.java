package com.alten.hercules.model.project;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.alten.hercules.model.exception.InvalidValueException;
import com.alten.hercules.model.mission.MissionSheet;
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

	public Date getPicture() { return picture; }
	public void setpicture(String picture) { this.picture = picture; }
	
	public MissionSheet getMissionSheet() { return missionSheet; }
	
}
