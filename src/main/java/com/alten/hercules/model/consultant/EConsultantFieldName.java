package com.alten.hercules.model.consultant;

/**
 * Field names enumeration of the Consultant entity
 * 
 */
public enum EConsultantFieldName {
	diplomas("diplomas"),
	email("email"),
	firstname("firstname"),
	lastname("lastname"),
	manager("manager"),
	releaseDate("releaseDate"),
	xp("xp");
			
	private String name;
	   
	EConsultantFieldName(String name) { this.name = name; }
	
	@Override
	public String toString(){ return name; }
	
}
