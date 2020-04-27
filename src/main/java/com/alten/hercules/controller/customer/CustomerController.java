package com.alten.hercules.controller.customer;


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
import com.alten.hercules.model.customer.response.BasicCustomerResponse;
import com.alten.hercules.model.request.customer.AddCustomerRequest;


@CrossOrigin(origins="*")
@RestController
@RequestMapping("/hercules/customers")
public class CustomerController {
	
	@Autowired
    private CustomerDAO dao;
		
	
	@GetMapping("")
	public ResponseEntity<Object> getAllCustomer(@RequestParam(required = false) Boolean basic) {
		if (basic == null || !basic)
			return ResponseEntity.ok(dao.findAll());
			List<BasicCustomerResponse> customers = new ArrayList<>();
			dao.findAll().forEach((customer) -> {
				customers.add(new BasicCustomerResponse(customer)); });;

		return ResponseEntity.ok(customers);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Optional<Customer>> getCustomerById(@PathVariable Long id)
    {
    	
		Optional<Customer> customer = dao.findById(id);
		
		if(id == null || !customer.isPresent())
		{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		return ResponseEntity.ok().body(customer);	
		
    }
	
	
	
	@PostMapping
    public ResponseEntity<?> addCustomer(@Valid @RequestBody AddCustomerRequest request) {
		Optional<Customer> optCustomer = dao.findByNameIgnoreCase(request.getName());
		if (optCustomer.isPresent())
			return ResponseEntity.accepted().body(optCustomer.get().getId());
		
		Customer customer = request.buildCustomer();
		dao.save(customer);
		return ResponseEntity.status(HttpStatus.CREATED).body(customer.getId());
	} 
	
	
	@PutMapping
    public ResponseEntity<?> updateCustomer(@Valid @RequestBody Customer customer) { 
		
		if (this.dao.findById(customer.getId()) == null)
		 {
			 return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		 }
		
		if(customer.getName()==null || customer.getName().isEmpty())
		{
			return ResponseEntity.noContent().build();
		}
		 
		 dao.save(customer);
		 return new ResponseEntity<>(HttpStatus.OK);
		 
	}
	
	 @DeleteMapping("/{id}")
	 public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
		 Optional<Customer> optCustomer = dao.findById(id);
			if (!optCustomer.isPresent())
				 return ResponseEntity.notFound().build();
			
		Customer customer = optCustomer.get();
		if (!customer.getMissions().isEmpty())
			return new ResponseEntity<>(HttpStatus.CONFLICT);

		 dao.delete(customer);
		 return ResponseEntity.ok().build();
	 }
	
	@GetMapping("/search")
	public List<Customer> searchCustomer(@RequestParam(name = "q") List<String> keys){
		List<Customer> customers = new ArrayList<>();
		for(String key : keys) {
			customers.addAll(this.dao.findByNameOrActivitySector(key));
		}
		List<Customer> listRes = new ArrayList<>(new HashSet<>(customers));
		return listRes;
	}
	
	
	  
	 
	
}
