package com.alten.hercules.model.project;

import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.validator.constraints.Length;

import com.alten.hercules.model.mission.OldMission;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Project {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = true)
	@Length(max=1000)
	private String description;
	
	@Column(nullable = true)
	private LocalDate beginDate;
	
	@Column(nullable = true)
	private LocalDate endDate;
	
	@Column(nullable = true)
	private Date lastUpdate;
	
	@JsonIgnore
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "mission_id", nullable = false)
	private OldMission mission;

	public Project() {
		super();
	}

	public Project(@Length(max = 1000) String description, LocalDate beginDate, LocalDate endDate, OldMission mission) {
		super();
		this.description = description;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.mission = mission;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(LocalDate beginDate) {
		this.beginDate = beginDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public OldMission getMission() {
		return mission;
	}

	public void setMission(OldMission mission) {
		this.mission = mission;
	}
}
