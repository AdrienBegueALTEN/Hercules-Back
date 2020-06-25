package com.alten.hercules.model.customer;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.alten.hercules.model.mission.Mission;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Class model for a customer.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
@Entity
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Customer {
	
	/**
	 * ID of the customer
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	/**
	 * Name of the customer
	 */
	@Column(nullable = false, unique = true)
	private String name;
	
	/**
	 * Activity sector of the customer
	 */
	@Column(nullable = false)
	private String activitySector;
	
	/**
	 * A description about the customer
	 */
	@Column(length = 1000)
	private String description = null;
	
	/**
	 * Path of the logo of the customer
	 */
	private String logo = null;
	
	/**
	 * Set of missions of the customer
	 */
	@OneToMany(mappedBy="customer")
	private Set<Mission> missions;
	
	/**
	 * Empty constructor
	 */
	public Customer() {}
	
	/**
	 * Simplified constructor
	 */
	public Customer(String name, String activitySector, String description) {
		this.name = name;
		this.activitySector = activitySector;
		this.description = description;
	}

	/**
	 * Complete constructor
	 */
	public Customer(long id, String activitySector, String description, String name, String logo) {
		super();
		this.id = id;
		this.activitySector = activitySector;
		this.description = description;
		this.name = name;
		this.logo = logo;
	}

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }

	public String getActivitySector() { return activitySector; }
	public void setActivitySector(String activitySector) { this.activitySector = activitySector; }

	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getLogo() { return logo; }
	public void setLogo(String logo) { this.logo = logo; }
	
	public Set<Mission> getMissions() { return missions; }
	public void setMissions(Set<Mission> missions) { this.missions = missions; }

}
