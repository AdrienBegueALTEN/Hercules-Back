package com.alten.hercules.dal;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.hercules.dao.consultant.ConsultantDAO;
import com.alten.hercules.dao.user.RecruitmentOfficerDAO;
import com.alten.hercules.dao.user.UserDAO;
import com.alten.hercules.model.user.RecruitmentOfficer;

@Service
public class RecruitmentOfficerDAL {
	
	@Autowired
	private ConsultantDAO consultantDAO;
	
	@Autowired
	private RecruitmentOfficerDAO recruitmentOfficerDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	public boolean emailIsAvailable(String email) {
		return !(userDAO.existsByEmail(email) || consultantDAO.existsByEmail(email));
	}

	public RecruitmentOfficer save(RecruitmentOfficer recruitmentOfficer) { 
		return recruitmentOfficerDAO.save(recruitmentOfficer);
	}

	public List<RecruitmentOfficer> findAll() {
		return recruitmentOfficerDAO.findAll();
	}
	
	public Optional<RecruitmentOfficer> findById(Long id){
		return recruitmentOfficerDAO.findById(id);
	}
	
	public void delete(RecruitmentOfficer recruitmentOfficer) {
		recruitmentOfficerDAO.delete(recruitmentOfficer);
	}
}
