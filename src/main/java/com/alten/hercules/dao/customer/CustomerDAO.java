package com.alten.hercules.dao.customer;

import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.alten.hercules.model.customer.Customer;


@Repository
public interface CustomerDAO extends JpaRepository<Customer, Long> { 
	
	Customer findByName(String name);
	Customer save(Optional<Customer> customer);
	
	@Query(value = "SELECT * FROM Customer c WHERE c.name LIKE %?1% OR c.activity_sector LIKE %?1% ", nativeQuery = true)
	List<Customer> findByNameOrActivitySector(String key);
	
	
	boolean existsByNameOrActivitySector(String name, String activitySector); //
	boolean existsByName(String name);

}
