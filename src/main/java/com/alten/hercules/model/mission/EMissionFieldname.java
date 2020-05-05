package com.alten.hercules.model.mission;

public enum EMissionFieldname {
	city("city"),
	comment("comment"),
	consultantStartXp("consultantStartXp"),
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
