package com.alten.hercules.controller.mission.http.response;

import java.util.Set;


import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.mission.ESheetStatus;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.mission.MissionSheet;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


public class BasicMissionResponse {

	private Long id;
	@JsonIgnoreProperties(value = {"missions"})
	private Consultant consultant;
	@JsonIgnoreProperties(value = {"missions"})
	private Customer customer;
	private Set<MissionSheet> versions;
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

