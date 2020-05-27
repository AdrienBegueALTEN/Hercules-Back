package com.alten.hercules.dal;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.hercules.dao.customer.CustomerDAO;
import com.alten.hercules.dao.mission.MissionDAO;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.mission.Mission;

@Service
public class CustomerDAL {
	@Autowired private CustomerDAO customerDAO;
	@Autowired private MissionDAO missionDAO;
	
	public List<Mission> findMissionsByCustomer(Long customerId){
		return this.missionDAO.findByCustomerId(customerId);
	}
	
	public Optional<Customer> findByNameIgnoreCase(String name){
		return this.customerDAO.findByNameIgnoreCase(name);
	}
	
	public List<Customer> findAll(){
		return this.customerDAO.findAll();
	}
	
	public Optional<Customer> findById(Long id){
		return this.customerDAO.findById(id);
	}
	
	public Customer save(Customer customer) {
		return this.customerDAO.save(customer);
	}
	
	public void delete(Customer customer) {
		this.customerDAO.delete(customer);
	}
	
}
