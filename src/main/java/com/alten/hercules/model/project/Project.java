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
	private Date beginDate;
	
	@Column(nullable = true)
	private Date endDate;
	
	@Column(nullable = true)
	private Date lastUpdate;

	public Project() {
		super();
	}

	public Project(String title, @Length(max = 1000) String description, Date beginDate, Date endDate) {
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

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
