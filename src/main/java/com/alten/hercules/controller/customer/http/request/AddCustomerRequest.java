package com.alten.hercules.controller.customer.http.request;

import javax.validation.constraints.NotBlank;

import com.alten.hercules.model.customer.Customer;

/**
 * Class that contains the information for a request that adds a customer.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class AddCustomerRequest {
	
	@NotBlank
	private String name;

	@NotBlank
	private String activitySector;
	
	private String description;
	
	public AddCustomerRequest(String name, String activitySector, String description) {
		this.name = name;
		this.activitySector = activitySector;
		this.description = description;
	}
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getActivitySector() { return activitySector; }
	public void setActivitySector(String activitySector) { this.activitySector = activitySector; }

	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }

	public Customer buildCustomer() {
		return new Customer(name, activitySector, description);
	}
}
