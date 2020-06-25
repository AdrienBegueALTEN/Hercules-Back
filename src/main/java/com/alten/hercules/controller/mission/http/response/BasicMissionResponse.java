package com.alten.hercules.controller.mission.http.response;

import java.util.Set;


import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.mission.ESheetStatus;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.mission.MissionSheet;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Class that contains the information for the response of a request that asks for the basic details of a mission.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class BasicMissionResponse {
	
	/**
	 * ID of the mission
	 */
	private Long id;
	
	/**
	 * Object Consultant of the mission
	 */
	@JsonIgnoreProperties(value = {"missions"})
	private Consultant consultant;
	
	/**
	 * Object Customer of the mission
	 */
	@JsonIgnoreProperties(value = {"missions"})
	private Customer customer;
	
	/**
	 * Set of the different versions of the mission
	 */
	private Set<MissionSheet> versions;
	
	/**
	 * Status of the mission 
	 */
	private ESheetStatus sheetStatus;
	
	public BasicMissionResponse(Mission mission) {
		this.id = mission.getId();
		this.consultant = mission.getConsultant();
		this.customer = mission.getCustomer();
		this.versions = mission.getVersions();
		this.sheetStatus = mission.getSheetStatus();
	}
	
	public Consultant getConsultant() { return consultant; }
	public Customer getCustomer() { return customer; }
	public Long getId() { return id; }
	public ESheetStatus getSheetStatus() { return sheetStatus; }
	public Set<MissionSheet> getVersions() { return versions; }

}

