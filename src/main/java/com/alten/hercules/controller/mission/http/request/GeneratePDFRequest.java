package com.alten.hercules.controller.mission.http.request;



public class GeneratePDFRequest {
	
	
	private Long id;
	
	private Long customer;
	
	
	private Long consultant;

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
	
}
