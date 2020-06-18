package com.alten.hercules.controller.mission.http.request;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

/**
 * Class that contains the information for a request that adds a mission.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class AddMissionRequest {
	
	@ApiModelProperty("Customer identifier.")
	@NotNull
	private Long customer;
	
	@ApiModelProperty("Consultant identifier.")
	@NotNull
	private Long consultant;

	public AddMissionRequest(Long customer, Long consultant) {
		this.customer = customer;
		this.consultant = consultant;
	}

	public Long getCustomer() {return customer; }
	public void setCustomer(Long customer) { this.customer = customer; }

	public Long getConsultant() { return consultant; }
	public void setConsultant(Long consultant) { this.consultant = consultant; }
}
