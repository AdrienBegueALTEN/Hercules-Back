package com.alten.hercules.dal;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.hercules.dao.user.ManagerDAO;
import com.alten.hercules.dao.user.UserDAO;
import com.alten.hercules.model.user.Manager;

@Service
public class AuthenticationDAL {
	
	@Autowired private ManagerDAO managerDAO;
	@Autowired private UserDAO userDAO;
	
	public Optional<Manager> findManagerById(long id) {
		return managerDAO.findById(id);
	}
	
	public boolean userExistsByEmail(String email) {
		return userDAO.existsByEmail(email);
	}
	
	public void saveManager(Manager manager) {
		managerDAO.save(manager);
	}

	public void deleteManager(Manager manager) {
		managerDAO.delete(manager);
	}
	
	public List<Manager> findAllManagers(){
		return this.managerDAO.findAll();
	}

}
