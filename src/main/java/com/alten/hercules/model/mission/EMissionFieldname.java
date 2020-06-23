package com.alten.hercules.model.mission;

/**
 * Field's name enumeration for a Mission.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public enum EMissionFieldname {
	city("city"),
	comment("comment"),
	consultantRole("consultantRole"),
	consultantStartXp("consultantStartXp"),
	contractType("contractType"),
	country("country"),
	description("description"),
	sheetStatus("sheetStatus"),
	teamSize("teamSize"),
	title("title");
	
	private String name;
	   
	EMissionFieldname(String name) { this.name = name; }
	
	@Override
	public String toString(){ return name; }
}
