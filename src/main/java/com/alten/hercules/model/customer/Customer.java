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
	private long customer_id;
	
	private String activitysector;
	
	private String description;
	
	private String name;
	
	private byte[] logo;
	

	public Customer() {
		super();
	}


	public Customer(long customer_id, String activitysector, String description, String name, byte[] logo) {
		super();
		this.customer_id = customer_id;
		this.activitysector = activitysector;
		this.description = description;
		this.name = name;
		this.logo = logo;
	}

	
	


	public long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(long customer_id) {
		this.customer_id = customer_id;
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

	public String getActivitysector() {
		return activitysector;
	}


	public void setActivitysector(String activitysector) {
		this.activitysector = activitysector;
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
