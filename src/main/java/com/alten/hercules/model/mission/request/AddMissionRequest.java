package com.alten.hercules.model.mission.request;

import javax.validation.constraints.NotNull;

public class AddMissionRequest {
	
	@NotNull
	private Long customer;
	
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
