package com.alten.hercules.model.user;

/**
 * Field's name enumeration for a Manager.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public enum EManagerFieldName {
	firstname("firstname"),
	lastname("lastname"),
	email("email"),
	releaseDate("releaseDate"),
	isAdmin("isAdmin");
	
	private String name;
	   
	EManagerFieldName(String name) { this.name = name; }
	
	@Override
	public String toString(){ return name; }
}
