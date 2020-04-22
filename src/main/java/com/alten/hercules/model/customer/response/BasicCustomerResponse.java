package com.alten.hercules.model.customer.response;

import com.alten.hercules.model.customer.Customer;

public class BasicCustomerResponse {
	
	private Long id;
	
	private String name;
	
	public BasicCustomerResponse(Customer customer) {
		this.id = customer.getId();
		this.name = customer.getName();
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

}
