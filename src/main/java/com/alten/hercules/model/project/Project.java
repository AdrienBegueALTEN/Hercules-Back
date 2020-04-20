package com.alten.hercules.model.project;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.validator.constraints.Length;

@Entity
public class Project {
	@Id
	private Long id;
	
	@Length(max=1000)
	private String description;
	private Date beginDate;
	private Date endDate;
	
	private Long missionId;
}
