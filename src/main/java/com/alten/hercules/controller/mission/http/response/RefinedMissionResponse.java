package com.alten.hercules.controller.mission.http.response;

import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.mission.MissionSheet;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class RefinedMissionResponse {

	@JsonIgnoreProperties(value = {"diplomas", "email", "experience", "id", "manager", "missions"})
	private Consultant consultant;
	@JsonIgnoreProperties(value = {"activitySector", "description", "id", "missions"})
	private Customer customer;
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