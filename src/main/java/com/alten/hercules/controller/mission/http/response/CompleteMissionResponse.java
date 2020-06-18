package com.alten.hercules.controller.mission.http.response;

import java.util.Set;

import com.alten.hercules.controller.consultant.http.response.ConsultantResponse;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.mission.ESheetStatus;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.mission.MissionSheet;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Class that contains the information for the response of a request that asks for all the details of a mission.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CompleteMissionResponse {

	@JsonIgnoreProperties(value = {"missions"})
	private ConsultantResponse consultant;
	@JsonIgnoreProperties(value = {"missions"})
	private Customer customer;
	private Long id;
	private MissionSheet lastVersion;
	private ESheetStatus sheetStatus;
	private Set<MissionSheet> versions;
	
	public CompleteMissionResponse(Mission mission, boolean allVersions, boolean sheetStatus) {
		this.id = mission.getId();
		this.consultant = new ConsultantResponse(mission.getConsultant());
		this.customer = mission.getCustomer();
		if (allVersions) this.versions = mission.getVersions();
		else this.lastVersion = mission.getLastVersion();
		if (sheetStatus) this.sheetStatus = mission.getSheetStatus();
	}
	
	public ConsultantResponse getConsultant() { return consultant; }
	public Customer getCustomer() { return customer; }
	public Long getId() { return id; }
	public MissionSheet getLastVersion() {return lastVersion; }
	public ESheetStatus getSheetStatus() { return sheetStatus; }
	public Set<MissionSheet> getVersions() { return versions; }

}
