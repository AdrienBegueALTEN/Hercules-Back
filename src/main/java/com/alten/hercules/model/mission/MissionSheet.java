package com.alten.hercules.model.mission;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.Length;

import com.alten.hercules.model.project.Project;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MissionSheet {
	
	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@JsonIgnore
	@ManyToOne
	private Mission mission;

	@Column(nullable = true)
	private Date versionDate;
	
	@Column(nullable = true)
	private String title = null;
	
	@Column(nullable = true)
	@Length(max = 1000)
	private String description = null;
	
	@Column(nullable = true)
	private String comment = null;
	
	@Column(nullable = true)
	private String city;
	
	@Column(nullable = true)
	private String country;
	
	@Min(0)
	@Column(nullable = true)
	private Integer consultantStartXp;
	
	@Min(1)
	@Column(nullable = true)
	private Integer teamSize;
	
	@Column(nullable = true)
	@Enumerated(EnumType.STRING)
	private EContractType contractType;
	
	@OneToMany
	private Set<Project> projects = new HashSet<>();
	
	public MissionSheet() {}
	
	public MissionSheet(Mission mission) {
		this.mission = mission;
	}
	
	public MissionSheet(MissionSheet sheet, Date versionDate) {
		setMission(sheet.mission);
		setVersionDate(versionDate);
		setTitle(sheet.getTitle());
		setDescription(sheet.getDescription());
		setComment(sheet.getComment());
		setCity(sheet.getCity());
		setCountry(sheet.getCountry());
		setConsultantStartXp(sheet.getConsultantStartXp());
		setContractType(sheet.getContractType());
		setTeamSize(sheet.getTeamSize());
		setProjects(sheet.getProjects());
	}
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id;}

	public Mission getMission() { return mission; }
	public void setMission(Mission mission) { this.mission = mission; }

	public Date getVersionDate() { return versionDate; }
	public void setVersionDate(Date versionDate) {this.versionDate = versionDate; }

	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }

	public String getComment() { return comment; }
	public void setComment(String comment) { this.comment = comment; }
	
	public String getCity() { return city; }
	public void setCity(String city) { this.city = city; }

	public String getCountry() { return country; }
	public void setCountry(String country) { this.country = country; }

	public Integer getConsultantStartXp() { return consultantStartXp; }
	public void setConsultantStartXp(Integer consultantStartXp) { this.consultantStartXp = consultantStartXp; }
	
	public EContractType getContractType() { return contractType; }
	public void setContractType(EContractType type) { this.contractType = type; }

	public Integer getTeamSize() { return teamSize; }
	public void setTeamSize(Integer teamSize) { this.teamSize = teamSize; }
	
	public Set<Project> getProjects() { return projects; }
	public void setProjects(Set<Project> projects) { this.projects = projects; }
}
