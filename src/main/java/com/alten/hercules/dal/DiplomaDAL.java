package com.alten.hercules.dal;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.hercules.dao.consultant.ConsultantDAO;
import com.alten.hercules.dao.diploma.DiplomaDAO;
import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.diploma.Diploma;

@Service
public class DiplomaDAL {
	
	@Autowired private ConsultantDAO consultantDAO;
	@Autowired private DiplomaDAO diplomaDAO;
	
	public Optional<Consultant> findConsultantById(Long id) {
		return consultantDAO.findById(id);
	}
	
	public Diploma save(Diploma diploma) {
		return diplomaDAO.save(diploma);
	}
	
	public void delete(Diploma diploma) {
		diplomaDAO.delete(diploma);
	}

	public Consultant saveConsultant(Consultant consultant) {
		return consultantDAO.save(consultant);
	}
}
