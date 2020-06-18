package com.alten.hercules.controller.customer.http.response;

import com.alten.hercules.model.customer.Customer;

/**
 * Class that contains the information of the response of a request that asks for the details of a customer.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class BasicCustomerResponse {
	
	private Long id;
	private String name;
	private String activitySector;
	
	public BasicCustomerResponse(Customer customer) {
		this.id = customer.getId();
		this.name = customer.getName();
		this.activitySector = customer.getActivitySector();
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getActivitySector() { return activitySector; }
	public void setActivitySector(String activitySector) { this.activitySector = activitySector; }
}
