package com.alten.hercules.controller.mission.http.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.mission.ESheetStatus;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.mission.MissionSheet;

public class MissionDetailsResponse {

	private Long id;
	private Map<String, Object> consultant = new HashMap<String, Object>();
	private Map<String, Object> customer = new HashMap<String, Object>();
	private Set<MissionSheet> versions;
	private ESheetStatus sheetStatus;
	
	public MissionDetailsResponse(Mission mission) {
		this.id = mission.getId();
		fillConsultant(mission);
		fillCustomer(mission.getCustomer());
		this.versions = mission.getVersions();
		this.sheetStatus = mission.getSheetStatus();
	}
	
	public Map<String, Object> getConsultant() { return consultant; }
	public void setConsultant(Map<String, Object> consultant) { this.consultant = consultant; }
	
	public Map<String, Object> getCustomer() { return customer; }
	public void setCustomer(Map<String, Object> customer) { this.customer = customer; }

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public ESheetStatus getSheetStatus() { return sheetStatus; }
	public void setSheetStatus(ESheetStatus sheetStatus) { this.sheetStatus = sheetStatus; }
	
	public Set<MissionSheet> getVersions() { return versions; }
	public void setVersions(Set<MissionSheet> versions) { this.versions = versions; }

	private void fillConsultant(Mission mission) {
		Consultant consultant = mission.getConsultant();
		Map<String, String> diplomas = new HashMap<String, String>();
		consultant.getDiplomas().forEach(diploma -> {
			diplomas.put("city", diploma.getDiplomaLocation().getCity());
			diplomas.put("name", diploma.getDiplomaName().getName());
			diplomas.put("school", diploma.getDiplomaLocation().getSchool());
		});
		this.consultant.put("diplomas", diplomas);
		this.consultant.put("email", consultant.getEmail());
		this.consultant.put("experience", consultant.getExperience());
		this.consultant.put("firstname", consultant.getFirstname());
		this.consultant.put("id", consultant.getId());
		this.consultant.put("lastname", consultant.getLastname());
		this.consultant.put("manager", consultant.getManager().getId());
		this.consultant.put("releaseDate", consultant.getReadableReleaseDate());
	}
	
	private void fillCustomer(Customer customer) {
		this.customer.put("activitySector", customer.getActivitySector());
		this.customer.put("description", customer.getDescription());
		this.customer.put("id", customer.getId());
		this.customer.put("logo", customer.getLogo());
		this.customer.put("name", customer.getName());
	}
}
