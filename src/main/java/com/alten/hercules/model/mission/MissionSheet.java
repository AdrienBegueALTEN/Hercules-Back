package com.alten.hercules.model.mission;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.Min;

import com.alten.hercules.model.exception.InvalidValueException;
import com.alten.hercules.model.project.Project;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Class model for a mission sheet.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
@Entity
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MissionSheet {
	
	@Column(nullable = false, columnDefinition = "VARCHAR(100) default ''")
	private String city = "";
	
	@Column(nullable = false, columnDefinition = "VARCHAR(255) default ''")
	private String comment = "";

	@Column(nullable = false, columnDefinition = "VARCHAR(50) default ''")
	private String consultantRole = "";
	
	@Min(0)
	private Integer consultantStartXp;
	
	@Enumerated(EnumType.STRING)
	private EContractType contractType;
	
	@Column(nullable = false, columnDefinition = "VARCHAR(100) default ''")
	private String country = "";;
	
	@Column(nullable = false, columnDefinition = "VARCHAR(1000) default ''")
	private String description = "";
	
	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@JsonIgnore
	@ManyToOne
	private Mission mission;
	
	@OrderBy("beginDate")
	@OneToMany(fetch = FetchType.LAZY, mappedBy="missionSheet", cascade = CascadeType.ALL)
	private Set<Project> projects = new HashSet<>();
	
	@Min(1)
	private Integer teamSize;
	
	@Column(nullable = false, columnDefinition = "VARCHAR(100) default ''")
	private String title = "";
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Column(nullable = true)
	private LocalDate versionDate;
	
	public MissionSheet() {}
	
	public MissionSheet(Mission mission) {
		this.mission = mission;
	}
	
	public MissionSheet(MissionSheet sheet) throws InvalidValueException {
		setMission(sheet.mission);
		setVersionDate(LocalDate.now());
		setTitle(sheet.getTitle());
		setDescription(sheet.getDescription());
		setComment(sheet.getComment());
		setCity(sheet.getCity());
		setCountry(sheet.getCountry());
		setConsultantStartXp(sheet.getConsultantStartXp());
		setContractType(sheet.getContractType());
		setTeamSize(sheet.getTeamSize());
		setProjects(
				sheet.getProjects().stream()
				.map(project -> new Project(project, this))
				.collect(Collectors.toSet()));
	}
	
	public String getCity() { return city; }
	public void setCity(String city) { this.city = city; }

	public String getComment() { return comment; }
	public void setComment(String comment) { this.comment = comment; }

	public String getConsultantRole() { return consultantRole; }
	public void setConsultantRole(String consultantRole) { this.consultantRole = consultantRole; }
	
	public Integer getConsultantStartXp() { return consultantStartXp; }
	public void setConsultantStartXp(Integer consultantStartXp) throws InvalidValueException {
		if (consultantStartXp < 0) throw new InvalidValueException();
		this.consultantStartXp = consultantStartXp;
	}

	public EContractType getContractType() { return contractType; }
	public void setContractType(EContractType contractType) { this.contractType = contractType; }

	public String getCountry() { return country; }
	public void setCountry(String country) { this.country = country; }

	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public Mission getMission() { return mission; }
	public void setMission(Mission mission) { this.mission = mission; }

	
	public Set<Project> getProjects() { return projects; }
	public void setProjects(Set<Project> projects) { this.projects = projects; }
	
	public Integer getTeamSize() { return teamSize; }
	public void setTeamSize(Integer teamSize) throws InvalidValueException {
		if (teamSize < 1) throw new InvalidValueException();
		this.teamSize = teamSize; 
	}

	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }

	public LocalDate getVersionDate() { return versionDate; }
	public void setVersionDate(LocalDate versionDate) { this.versionDate = versionDate; }
	
	public void addProject(Project project) {
		projects.add(project);
	}
	
	public void removeProject(Project project) {
		projects.remove(project);
	}
}
