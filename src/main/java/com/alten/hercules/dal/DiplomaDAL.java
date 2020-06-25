package com.alten.hercules.dal;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.hercules.dao.consultant.ConsultantDAO;
import com.alten.hercules.dao.diploma.DiplomaDAO;
import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.diploma.Diploma;

/**
 * Layer to access the DAL needed for the diplomas.
 * @author rjesson, mfoltz, abegue, jbaudot
 *
 */
@Service
public class DiplomaDAL {
	
	/**
	 * DAO for consultants
	 */
	@Autowired private ConsultantDAO consultantDAO;
	
	/**
	 * DAO for diplomas
	 */
	@Autowired private DiplomaDAO diplomaDAO;
	
	/**
	 * Find a consultant by the id.
	 * @param id  Consultant id
	 * @return Optional consutant object
	 */
	public Optional<Consultant> findConsultantById(Long id) {
		return consultantDAO.findById(id);
	}
	
	/**
	 * Create or update a diploma.
	 * @param diploma Diploma object
	 * @return Created or updated diploma
	 */
	public Diploma save(Diploma diploma) {
		return diplomaDAO.save(diploma);
	}
	
	/**
	 * Delete a diploma.
	 * @param diploma  Diploma object to delete
	 */
	public void delete(Diploma diploma) {
		diplomaDAO.delete(diploma);
	}

	/**
	 * Update a consultant.
	 * @param consultant  Consultant object
	 * @return Updated consultant object
	 */
	public Consultant saveConsultant(Consultant consultant) {
		return consultantDAO.save(consultant);
	}

	/**
	 * Find a diploma by its id.
	 * @param id  Diploma id
	 * @return Optional diploma object
	 */
	public Optional<Diploma> findById(Long id) {
		return diplomaDAO.findById(id);
	}
}
