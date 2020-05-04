package com.alten.hercules.controller.mission.http.response;

import java.util.Set;

import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.mission.ESheetStatus;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.mission.MissionSheet;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class MissionDetailsResponse {

	private Long id;
	@JsonIgnoreProperties(value = {"missions"})
	private Consultant consultant;
	@JsonIgnoreProperties(value = {"missions"})
	private Customer customer;
	private Set<MissionSheet> versions;
	private ESheetStatus sheetStatus;
	
	public MissionDetailsResponse(Mission mission) {
		this.id = mission.getId();
		this.consultant = mission.getConsultant();
		this.customer = mission.getCustomer();
		this.versions = mission.getVersions();
		this.sheetStatus = mission.getSheetStatus();
	}
	
	public Consultant getConsultant() { return consultant; }
	public void setConsultant(Consultant consultant) { this.consultant = consultant; }
	
	public Customer getCustomer() { return customer; }
	public void setCustomer(Customer customer) { this.customer = customer; }

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public ESheetStatus getSheetStatus() { return sheetStatus; }
	public void setSheetStatus(ESheetStatus sheetStatus) { this.sheetStatus = sheetStatus; }
	
	public Set<MissionSheet> getVersions() { return versions; }
	public void setVersions(Set<MissionSheet> versions) { this.versions = versions; }
}
