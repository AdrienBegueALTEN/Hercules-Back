package com.alten.hercules.model.customer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Customer {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String activitySector;
	
	private String description = null;
	
	private byte[] logo = null;
	
	public Customer() {}


	public Customer(String name, String activitySector, String description) {
		this.name = name;
		this.activitySector = activitySector;
		this.description = description;
	}


	public Customer(long id, String activitySector, String description, String name, byte[] logo) {
		super();
		this.id = id;
		this.activitySector = activitySector;
		this.description = description;
		this.name = name;
		this.logo = logo;
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getActivitySector() {
		return activitySector;
	}


	public void setActivitySector(String activitySector) {
		this.activitySector = activitySector;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public byte[] getLogo() {
		return logo;
	}


	public void setLogo(byte[] logo) {
		this.logo = logo;
	}
	
	


	
	
}
