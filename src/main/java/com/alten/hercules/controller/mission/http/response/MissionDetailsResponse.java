package com.alten.hercules.controller.mission.http.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.mission.ESheetStatus;
import com.alten.hercules.model.mission.EType;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.mission.MissionSheet;

public class MissionDetailsResponse {

	private String city;
	private Map<String, Object> consultant = new HashMap<String, Object>();
	private Integer consultantStartXp;
	private String country;
	private Map<String, Object> customer = new HashMap<String, Object>();
	private Long id;
	private ESheetStatus sheetStatus;
	private Integer teamSize;
	private EType type;
	private Set<MissionSheet> versions;
	
	public MissionDetailsResponse(Mission mission) {
		this.city = mission.getCity();
		fillConsultant(mission);
		this.country = mission.getCountry();
		fillCustomer(mission.getCustomer());
		this.id = mission.getId();
		this.sheetStatus = mission.getSheetStatus();
		this.teamSize = mission.getTeamSize();
		this.type = mission.getType();
		this.versions = mission.getVersions();
	}
	
	public String getCity() { return city; }
	public void setCity(String city) { this.city = city; }
	
	public Map<String, Object> getConsultant() { return consultant; }
	public void setConsultant(Map<String, Object> consultant) { this.consultant = consultant; }

	public Integer getConsultantStartXp() { return consultantStartXp; }
	public void setConsultantStartXp(Integer consultantStartXp) { this.consultantStartXp = consultantStartXp; }

	public String getCountry() { return country; }
	public void setCountry(String country) { this.country = country; }
	
	public Map<String, Object> getCustomer() { return customer; }
	public void setCustomer(Map<String, Object> customer) { this.customer = customer; }

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public ESheetStatus getSheetStatus() { return sheetStatus; }
	public void setSheetStatus(ESheetStatus sheetStatus) { this.sheetStatus = sheetStatus; }

	public Integer getTeamSize() { return teamSize; }
	public void setTeamSize(Integer teamSize) { this.teamSize = teamSize; }

	public EType getType() { return type; }
	public void setType(EType type) { this.type = type; }
	
	public Set<MissionSheet> getVersions() { return versions; }
	public void setVersions(Set<MissionSheet> versions) { this.versions = versions; }

	private void fillConsultant(Mission mission) {
		Consultant consultant = mission.getConsultant();
		this.consultant.put("startXp", mission.getConsultantStartExp());
		Map<String, String> diplomas = new HashMap<String, String>();
		consultant.getDiplomas().forEach(diploma -> {
			diplomas.put("city", diploma.getDiplomaLocation().getCity());
			diplomas.put("name", diploma.getDiplomaName().getName());
			diplomas.put("school", diploma.getDiplomaLocation().getSchool());
		});
		this.consultant.put("diplomas", diplomas);
		this.consultant.put("email", consultant.getEmail());
		this.consultant.put("xp", consultant.getExperience());
		this.consultant.put("firstname", consultant.getFirstname());
		this.consultant.put("id", consultant.getId());
		this.consultant.put("lastname", consultant.getLastname());
		this.consultant.put("manager", consultant.getManager().getId());
		this.consultant.put("releaseDate", consultant.getReadableReleaseDate());
	}
	
	private void fillCustomer(Customer customer) {
		this.customer.put("activitySector", customer.getActivitySector());
		this.customer.put("id", customer.getId());
		this.customer.put("logo", customer.getLogo());
		this.customer.put("name", customer.getName());
	}
}
