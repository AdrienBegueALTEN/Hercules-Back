package com.alten.hercules.dal;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.hercules.dao.consultant.ConsultantDAO;
import com.alten.hercules.dao.user.ManagerDAO;
import com.alten.hercules.dao.user.UserDAO;
import com.alten.hercules.model.user.Manager;

@Service
public class ManagerDAL {
	
	@Autowired
	private ConsultantDAO consultantDAO;
	
	@Autowired
	private ManagerDAO managerDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	public boolean emailIsAvailable(String email) {
		return !(userDAO.existsByEmail(email) || consultantDAO.existsByEmail(email));
	}

	public Manager save(Manager manager) { 
		return managerDAO.save(manager);
	}

	public List<Manager> findAll() {
		return managerDAO.findAll();
	}
	
	public List<Manager> findAllActive() {
		return managerDAO.findByReleaseDateIsNull();
	}
	
	public Optional<Manager> findById(Long id){
		return managerDAO.findById(id);
	}
	
	public void delete(Manager manager) {
		managerDAO.delete(manager);
	}
}
