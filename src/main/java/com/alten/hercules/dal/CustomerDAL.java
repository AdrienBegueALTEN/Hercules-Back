package com.alten.hercules.dal;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.hercules.dao.customer.CustomerDAO;
import com.alten.hercules.dao.mission.MissionDAO;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.mission.Mission;

/**
 * Layer to access the DAL needed for the customers.
 * @author rjesson, mfoltz, abegue, jbaudot
 *
 */
@Service
public class CustomerDAL {
	@Autowired private CustomerDAO customerDAO;
	@Autowired private MissionDAO missionDAO;
	
	/**
	 * List of the missions linked to a customer.
	 * @param customerId  Customer id
	 * @return List of missions
	 */
	public List<Mission> findMissionsByCustomer(Long customerId){
		return this.missionDAO.findByCustomerId(customerId);
	}
	
	/**
	 * Give a customer object corresponding to the given name, not taking care of the case.
	 * @param name  customer name
	 * @return  Optional customer object
	 */
	public Optional<Customer> findByNameIgnoreCase(String name){
		return this.customerDAO.findByNameIgnoreCase(name);
	}
	
	/**
	 * List of all customer.
	 * @return  List of customer
	 */
	public List<Customer> findAll(){
		return this.customerDAO.findAll();
	}
	
	/**
	 * Give a customer corresponding to the given id.
	 * @param id  Customer id
	 * @return Optional customer object
	 */
	public Optional<Customer> findById(Long id){
		return this.customerDAO.findById(id);
	}
	
	/**
	 * Save or update a customer in the database.
	 * @param customer  Csutomer object
	 * @return Saved or updated customer
	 */
	public Customer save(Customer customer) {
		return this.customerDAO.save(customer);
	}
	
	/**
	 * Delete a customer from the database.
	 * @param customer  Customer object to delete
	 */
	public void delete(Customer customer) {
		this.customerDAO.delete(customer);
	}
	
}
