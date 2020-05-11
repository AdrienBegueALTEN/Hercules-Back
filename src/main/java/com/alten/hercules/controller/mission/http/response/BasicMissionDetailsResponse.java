package com.alten.hercules.controller.mission.http.response;

import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.mission.MissionSheet;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class BasicMissionDetailsResponse {

	@JsonIgnoreProperties(value = {"diplomas", "email", "experience", "id", "manager", "missions"})
	private Consultant consultant;
	@JsonIgnoreProperties(value = {"activitySector", "description", "id", "missions"})
	private Customer customer;
	@JsonIgnoreProperties(value = {"comment", "versionDate"})
	private MissionSheet lastVersion;
	
	public BasicMissionDetailsResponse(Mission mission) {
		this.consultant = mission.getConsultant();
		this.customer = mission.getCustomer();
		this.lastVersion = (MissionSheet)mission.getVersions().toArray()[0];
	}
	
	public Consultant getConsultant() { return consultant; }
	public void setConsultant(Consultant consultant) { this.consultant = consultant; }
	
	public Customer getCustomer() { return customer; }
	public void setCustomer(Customer customer) { this.customer = customer; }
	
	public MissionSheet getLastVersion() { return lastVersion; }
	public void setlastVersion(MissionSheet lastVersion) { this.lastVersion = lastVersion; }
}
