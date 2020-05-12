package com.alten.hercules.model.project;

public enum EProjectFieldname {
	title("title"),
	description("description"),
	beginDate("beginDate"),
	endDate("endDate");
			
	private String name;
	   
	private EProjectFieldname(String name) { 
		this.name = name; 
	}
	
	@Override
	public String toString(){ 
		return name; 
	}
}
