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
	
	/**
	 * Object that contains the information of the consultant of the mission
	 */
	@JsonIgnoreProperties(value = {"missions"})
	private ConsultantResponse consultant;
	
	/**
	 * Object that contains the information of the customer of the mission
	 */
	@JsonIgnoreProperties(value = {"missions"})
	private Customer customer;
	
	/**
	 * ID of the mission
	 */
	private Long id;
	
	/**
	 * Last version of the mission
	 */
	private MissionSheet lastVersion;
	
	/**
	 * Status of the mission
	 */
	private ESheetStatus sheetStatus;
	
	/**
	 * Set of the different versions of the mission
	 */
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
