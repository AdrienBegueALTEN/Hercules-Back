package com.alten.hercules.controller.mission.http.response;

import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.mission.MissionSheet;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Class that contains the information for the response of a request that asks for specific details of a mission.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class RefinedMissionResponse {
	
	/**
	 * Object Consultant of the mission
	 */
	@JsonIgnoreProperties(value = {"diplomas", "email", "experience", "id", "manager", "missions"})
	private Consultant consultant;
	
	/**
	 * Object Customer of the mission
	 */
	@JsonIgnoreProperties(value = {"activitySector", "description", "id", "missions"})
	private Customer customer;
	
	/**
	 * Last version of the mission
	 */
	@JsonIgnoreProperties(value = {"comment", "versionDate"})
	private MissionSheet lastVersion;
	
	public RefinedMissionResponse(Mission mission) {
		this.consultant = mission.getConsultant();
		this.customer = mission.getCustomer();
		this.lastVersion = mission.getLastVersion();
	}
	
	public Consultant getConsultant() { return consultant; }
	public Customer getCustomer() { return customer; }
	public MissionSheet getLastVersion() { return lastVersion; }
}
