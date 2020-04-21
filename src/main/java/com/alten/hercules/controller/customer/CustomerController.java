package com.alten.hercules.controller.customer;


import java.net.URI;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alten.hercules.dao.customer.CustomerDAO;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.request.customer.AddCustomerRequest;


@CrossOrigin(origins="*")
@RestController
@RequestMapping("/hercules/customers")
public class CustomerController {
	
	
	
	@Autowired
    private CustomerDAO customerDAO;
		
	@GetMapping
    public List<Customer> getAllCustomers() {
        return customerDAO.findAll();
        
    }
	
	
	@GetMapping("/{customer_id}")
	public ResponseEntity<Optional<Customer>> getCustomerById(@PathVariable(value = "customer_id") Long customer_id)
    {
    	
		Optional<Customer> customer = customerDAO.findById(customer_id);
		
		if(customer_id == null || !customer.isPresent())
		{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		return ResponseEntity.ok().body(customer);	
		
    }
	
	
	
	@PostMapping
    public ResponseEntity<?> createCustomer(@Valid @RequestBody AddCustomerRequest request) {
		Customer customer = request.buildCustomer();
		customerDAO.save(customer);
		URI location = URI.create(String.format("/customers/%s", customer.getId()));
		return ResponseEntity.created(location).build();
	} 
	
	
	@PutMapping
    public ResponseEntity<?> updateCustomer(@Valid @RequestBody Customer customer) { 
		
		if (this.customerDAO.findById(customer.getId()) == null)
		 {
			 return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		 }
		
		if(customer.getName()==null || customer.getName().isEmpty())
		{
			return ResponseEntity.noContent().build();
		}
		 
		 customerDAO.save(customer);
		 return new ResponseEntity<>(HttpStatus.OK);
		 
	}
	
	 @DeleteMapping
	 public ResponseEntity<?> deleteCustomer(@Valid @RequestBody Customer customer)
	 {
		 if (customerDAO.findById(customer.getId()) == null)
		 {
			 return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			 
		 }
		 
		 customerDAO.delete(customer);
		 return new ResponseEntity<>(HttpStatus.OK);

	 }
	
	
	
	@GetMapping("/search")
	public List<Customer> searchCustomer(@RequestParam(name = "q") List<String> keys){
		List<Customer> customers = new ArrayList<>();
		for(String key : keys) {
			customers.addAll(this.customerDAO.findByNameOrActivitySector(key));
		}
		List<Customer> listRes = new ArrayList<>(new HashSet<>(customers));
		return listRes;
	}
	
	
	  
	 
	
}
