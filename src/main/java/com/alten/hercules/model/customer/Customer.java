package com.alten.hercules.model.customer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;





@Entity
@Table(name = "customer")
public class Customer {

	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String activitySector;
	
	private String description;
	
	private String name;
	
	private byte[] logo;
	

	public Customer() {
		super();
	}


	public Customer(String activitySector, String description, String name, byte[] logo) {
		super();
		this.activitySector = activitySector;
		this.description = description;
		this.name = name;
		this.logo = logo;
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
