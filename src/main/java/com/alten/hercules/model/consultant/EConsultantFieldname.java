package com.alten.hercules.model.consultant;

/**
 * Field names enumeration of the Consultant entity
 * @author mfoltz, rjesson, abegue, jbaudot
 * 
 */
public enum EConsultantFieldname {
	diplomas("diplomas"),
	email("email"),
	firstname("firstname"),
	lastname("lastname"),
	manager("manager"),
	releaseDate("releaseDate"),
	experience("experience");
			
	private String name;
	   
	EConsultantFieldname(String name) { this.name = name; }
	
	@Override
	public String toString(){ return name; }
}
