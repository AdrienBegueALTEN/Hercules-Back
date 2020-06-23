package com.alten.hercules.dao.customer;

import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.alten.hercules.model.customer.Customer;

/**
 * Interface that is inherited from JpaRepository and serves to make queries for the customers.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
@Repository
public interface CustomerDAO extends JpaRepository<Customer, Long> { 
	
	/**
	 * Query that looks for a customer by using his name and ignoring its case.
	 * @param name Name of the customer
	 * @return A corresponding customer if possible.
	 */
	Optional<Customer> findByNameIgnoreCase(String name);
	
	/**
	 * Query that looks for customers with the name's and activity sector's fields.
	 * @param key Name or activity sector
	 * @return A corresponding list of customers if possible.
	 */
	@Query(value = "SELECT * FROM Customer c WHERE c.name LIKE %?1% OR c.activity_sector LIKE %?1% ", nativeQuery = true)
	List<Customer> findByNameOrActivitySector(String key);
	
}
