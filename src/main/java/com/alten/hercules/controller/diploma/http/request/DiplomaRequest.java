package com.alten.hercules.controller.diploma.http.request;

import javax.validation.constraints.NotBlank;


public class DiplomaRequest {
	
	private Long id;
	private int graduationYear;
	private String graduationCity;
	private String school;
	private String diplomaName;
	private String levelName;
	private Long consultantId;
	
	
	
	public DiplomaRequest(Long id, int graduationYear, String graduationCity,
			String school,String diplomaName, String levelName, Long consultantId) {
		super();
		this.id = id;
		this.graduationYear = graduationYear;
		this.graduationCity = graduationCity;
		this.school = school;
		this.diplomaName = diplomaName;
		this.levelName = levelName;
		this.consultantId = consultantId;
	}

	public DiplomaRequest(int graduationYear, String graduationCity,
			 String diplomaName, String levelName, String school, Long consultantId) {
		super();
		this.graduationYear = graduationYear;
		this.graduationCity = graduationCity;
		this.diplomaName = diplomaName;
		this.levelName = levelName;
		this.school = school;
		this.consultantId = consultantId;
	}
	
	public DiplomaRequest() {
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getGraduationYear() {
		return graduationYear;
	}
	public void setGraduationYear(int graduationYear) {
		this.graduationYear = graduationYear;
	}
	public String getGraduationCity() {
		return graduationCity;
	}
	public void setGraduationCity(String graduationCity) {
		this.graduationCity = graduationCity;
	}
	public String getDiplomaName() {
		return diplomaName;
	}
	public void setDiplomaName(String diplomaName) {
		this.diplomaName = diplomaName;
	}
	public String getLevelName() {
		return levelName;
	}
	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}
	
	
	
	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public Long getConsultantId() {
		return consultantId;
	}

	public void setConsultantId(Long consultantId) {
		this.consultantId = consultantId;
	}

	
	

}