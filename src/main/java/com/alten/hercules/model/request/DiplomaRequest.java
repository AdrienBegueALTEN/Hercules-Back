package com.alten.hercules.model.request;

import javax.validation.constraints.NotBlank;

public class DiplomaRequest {
	@NotBlank
	private Long graduationYear;
	@NotBlank
	private String graduationCity;
	@NotBlank
	private String diplomaName;
	@NotBlank
	private String levelName;
	
	public DiplomaRequest(@NotBlank Long graduationYear, @NotBlank String graduationCity,
			@NotBlank String diplomaName, @NotBlank String levelName) {
		super();
		this.graduationYear = graduationYear;
		this.graduationCity = graduationCity;
		this.diplomaName = diplomaName;
		this.levelName = levelName;
	}
	
	public Long getGraduationYear() {
		return graduationYear;
	}
	public void setGraduationYear(Long graduationYear) {
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
	
	

}
