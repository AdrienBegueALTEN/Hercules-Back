package com.alten.hercules.dal;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.hercules.dao.consultant.ConsultantDAO;
import com.alten.hercules.dao.user.ManagerDAO;
import com.alten.hercules.dao.user.UserDAO;
import com.alten.hercules.model.user.Manager;

/**
 * Layer to access the DAL needed for the managers.
 * @author rjesson, mfoltz, abegue, jbaudot
 *
 */
@Service
public class ManagerDAL {
	
	/**
	 * DAO for consultants
	 */
	@Autowired
	private ConsultantDAO consultantDAO;
	
	/**
	 * DAO for managers
	 */
	@Autowired private ManagerDAO managerDAO;
	
	/**
	 * DAO for users
	 */
	@Autowired private UserDAO userDAO;
	
	/**
	 * Check if an email is available among all users and consutlants.
	 * @param email  String email to check
	 * @return True if the email is available, fals if not.
	 */
	public boolean emailIsAvailable(String email) {
		return !(userDAO.existsByEmail(email) || consultantDAO.existsByEmail(email));
	}

	/**
	 * Create or update a manzger in database.
	 * @param manager  Manager object
	 * @return Created or updated manager
	 */
	public Manager save(Manager manager) { 
		return managerDAO.save(manager);
	}

	/**
	 * List of all managers.
	 * @return List of all managers
	 */
	public List<Manager> findAll() {
		return managerDAO.findAll();
	}
	
	/**
	 * Find all active managers (release date is null or greater than current date).
	 * @return List of active managers
	 */
	public List<Manager> findAllActive() {
		return managerDAO.findByReleaseDateIsNullOrReleaseDateGreaterThan(LocalDate.now());
	}
	
	/**
	 * Find a manager by his id.
	 * @param id  Manager id
	 * @return Optional maanger object
	 */
	public Optional<Manager> findById(Long id){
		return managerDAO.findById(id);
	}
	
	/**
	 * Delete a manager from the database.
	 * @param manager  Manager to be deleted
	 */
	public void delete(Manager manager) {
		managerDAO.delete(manager);
	}
}
