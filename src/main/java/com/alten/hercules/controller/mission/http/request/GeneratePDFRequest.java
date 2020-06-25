package com.alten.hercules.controller.mission.http.request;

import javax.validation.constraints.NotNull;

/**
 * Class that contains the information for a request for the generation of a PDF file for the particular page of a project or a mission.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class GeneratePDFRequest {
	
	/**
	 * ID of the project or mission
	 */
	@NotNull
	private Long id;
	
	/**
	 * ID of the customer
	 */
	private Long customer;
	
	/**
	 * ID of the consultant
	 */
	private Long consultant;
	
	/**
	 * String that indicates the type of the page : project or mission
	 */
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
