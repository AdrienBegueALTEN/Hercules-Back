package com.alten.hercules.model.user;

public enum ERecruitmentOfficerFieldName {
	firstname("firstname"),
	lastname("lastname"),
	email("email"),
	releaseDate("releaseDate");
	
	private String name;
	   
	ERecruitmentOfficerFieldName(String name) { this.name = name; }
	
	@Override
	public String toString(){ return name; }
}
