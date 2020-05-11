package com.alten.hercules.dal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.hercules.controller.consultant.http.response.BasicConsultantResponse;
import com.alten.hercules.dao.consultant.ConsultantDAO;
import com.alten.hercules.dao.diploma.DiplomaDAO;
import com.alten.hercules.dao.user.ManagerDAO;
import com.alten.hercules.dao.user.UserDAO;
import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.diploma.Diploma;
import com.alten.hercules.model.user.Manager;

@Service
public class ConsultantDAL {
	
	@Autowired
	private ConsultantDAO consultantDAO;
	
	@Autowired
	private ManagerDAO managerDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private DiplomaDAO diplomaDAO;
	
	public ConsultantDAL() {}
	
	public List<BasicConsultantResponse> findAllEnabled() {
		List<BasicConsultantResponse> consultants = new ArrayList<>();
		consultantDAO.findByReleaseDateIsNull().forEach((consultant) -> {
			consultants.add(new BasicConsultantResponse(consultant)); });
		return consultants;
	}

	public Optional<Consultant> findByEmail(String email) {
		return consultantDAO.findByEmail(email);
	}
	
	public boolean emailIsAvailable(String email) {
		return !(userDAO.existsByEmail(email) || consultantDAO.existsByEmail(email));
	}

	public void save(Consultant consultant) { consultantDAO.save(consultant); }
	
	public Optional<Manager> findEnabledManager(Long id) {
		return managerDAO.findByIdAndReleaseDateIsNull(id);
	}
	
	public Optional<Diploma> findDiplomaById(Long id) {
		return diplomaDAO.findById(id);
	}
	
	public List<Consultant> findAll() {
		return consultantDAO.findAll();
	}
	
	public Optional<Consultant> findById(Long id){
		return consultantDAO.findById(id);
	}

	public void delete(Consultant consultant) {
		consultantDAO.delete(consultant);
	}
	
	public List<Consultant> findReleasedConsultantFiveyears(){
		return this.consultantDAO.findReleasedConsultantFiveyears();
	}
}
