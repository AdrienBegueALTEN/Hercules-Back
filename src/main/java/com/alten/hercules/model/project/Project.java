package com.alten.hercules.model.project;

import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.validator.constraints.Length;

@Entity
public class Project {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = true)
	private String title;
	
	@Column(nullable = true)
	@Length(max=1000)
	private String description;
	
	@Column(nullable = true)
	private LocalDate beginDate;
	
	@Column(nullable = true)
	private LocalDate endDate;
	
	@Column(nullable = true)
	private Date lastUpdate;

	public Project() {
		super();
	}

	public Project(String title, @Length(max = 1000) String description, LocalDate beginDate, LocalDate endDate) {
		super();
		this.title = title;
		this.description = description;
		this.beginDate = beginDate;
		this.endDate = endDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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
}
