package com.alten.hercules.controller.mission.http.request;

import javax.validation.constraints.NotNull;

public class GeneratePDFRequest {
	
	@NotNull
	private Long id;
	
	private Long customer;
	
	
	private Long consultant;
	
	@NotNull
	private String type;

	

	public GeneratePDFRequest(Long customer, Long consultant) {
		this.customer = customer;
		this.consultant = consultant;
	}

	public Long getCustomer() {return customer; }
	public void setCustomer(Long customer) { this.customer = customer; }

	public Long getConsultant() { return consultant; }
	public void setConsultant(Long consultant) { this.consultant = consultant; }
	
	public Long getId() {return id; }
	public void setId(Long id) { this.id = id; }
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
