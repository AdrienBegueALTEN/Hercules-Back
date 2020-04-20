package com.alten.hercules.dao.customer;

import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.alten.hercules.model.customer.Customer;


@Repository
public interface CustomerDAO extends JpaRepository<Customer, Long> { 
	
	List<Customer> findAll();
	Customer findByName(String name);
	Optional<Customer> findById(Long customer_id);
	Customer save(Optional<Customer> customer);
	
	@Query("SELECT c FROM Customer c WHERE c.name LIKE %?1% OR c.activitysector LIKE %?1% ")
	List<Customer> findByNameOrActivitysector(String key);
	
	
	boolean existsByNameOrActivitysector(String name, String activitysector); //
	boolean existsByName(String name);

}
