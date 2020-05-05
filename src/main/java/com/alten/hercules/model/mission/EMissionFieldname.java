package com.alten.hercules.model.mission;

public enum EMissionFieldname {
	city("city"),
	comment("comment"),
	consultantRole("consultantRole"),
	consultantStartXp("consultantStartXp"),
	contractType("contractType"),
	country("country"),
	description("description"),
	teamSize("teamSize"),
	title("title");
	
	private String name;
	   
	EMissionFieldname(String name) { this.name = name; }
	
	@Override
	public String toString(){ return name; }
}
