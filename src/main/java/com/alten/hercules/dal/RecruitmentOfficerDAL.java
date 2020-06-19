package com.alten.hercules.dal;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.hercules.dao.consultant.ConsultantDAO;
import com.alten.hercules.dao.user.RecruitmentOfficerDAO;
import com.alten.hercules.dao.user.UserDAO;
import com.alten.hercules.model.user.RecruitmentOfficer;

/**
 * Layer to access the DAL needed for the recruitment officers.
 * @author rjesson, mdoltz, abegue, jbaudot
 *
 */
@Service
public class RecruitmentOfficerDAL {
	
	@Autowired private ConsultantDAO consultantDAO;
	@Autowired private RecruitmentOfficerDAO recruitmentOfficerDAO;
	@Autowired private UserDAO userDAO;
	
	/**
	 * Check if an email is available among the consultants and users.
	 * @param email  String email to check
	 * @return True is the email ois available or false if not.
	 */
	public boolean emailIsAvailable(String email) {
		return !(userDAO.existsByEmail(email) || consultantDAO.existsByEmail(email));
	}

	/**
	 * Create or update a recruitment officer in the database.
	 * @param recruitmentOfficer  Recruitment officer to add or update
	 * @return Recruitment office object newly added or updated
	 */
	public RecruitmentOfficer save(RecruitmentOfficer recruitmentOfficer) { 
		return recruitmentOfficerDAO.save(recruitmentOfficer);
	}

	/**
	 * List of all recruitment officers.
	 * @return List of recruitment officer.
	 */
	public List<RecruitmentOfficer> findAll() {
		return recruitmentOfficerDAO.findAll();
	}
	
	/**
	 * Find a recruitment officer by his id.
	 * @param id Recruitment officer id
	 * @return Optional recruitment officer object
	 */
	public Optional<RecruitmentOfficer> findById(Long id){
		return recruitmentOfficerDAO.findById(id);
	}
	
	/**
	 * Delete from the database a recruitment officer.
	 * @param recruitmentOfficer Recruitment officer to delete
	 */
	public void delete(RecruitmentOfficer recruitmentOfficer) {
		recruitmentOfficerDAO.delete(recruitmentOfficer);
	}
}
