package com.alten.hercules.dal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.hercules.controller.consultant.http.response.BasicConsultantResponse;
import com.alten.hercules.dao.consultant.ConsultantDAO;
import com.alten.hercules.dao.diploma.DiplomaDAO;
import com.alten.hercules.dao.mission.MissionDAO;
import com.alten.hercules.dao.user.ManagerDAO;
import com.alten.hercules.dao.user.UserDAO;
import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.diploma.Diploma;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.user.Manager;

/**
 * Layer to access the DAL needed for the consultants.
 * @author rjesson, mdoltz, abegue, jbaudot
 *
 */
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
	
	@Autowired
	private MissionDAO missionDAO;
	
	public ConsultantDAL() {}
	
	/**
	 * List of all consultant still member of ALTEN
	 * @return List of all consultant whose release date is not set
	 */
	public List<BasicConsultantResponse> findAllEnabled() {
		LocalDate today = LocalDate.now();
		List<BasicConsultantResponse> consultants = new ArrayList<>();
		consultantDAO.findByReleaseDateIsNullOrReleaseDateGreaterThan(today).forEach((consultant) -> {
			consultants.add(new BasicConsultantResponse(consultant)); });
		return consultants;
	}

	/**
	 * Return the consultant corresponding to the given email.
	 * @param email  Email of the consultant
	 * @return Optional consultant
	 */
	public Optional<Consultant> findByEmail(String email) {
		return consultantDAO.findByEmail(email);
	}
	
	/**
	 * Check if the email is available in the database.
	 * @param email  Email string
	 * @return True if the email is available
	 */
	public boolean emailIsAvailable(String email) {
		return !(userDAO.existsByEmail(email) || consultantDAO.existsByEmail(email));
	}

	/**
	 * Save a consultant in the database.
	 * @param consultant  Consultant to save
	 */
	public void save(Consultant consultant) { consultantDAO.save(consultant); }
	
	/**
	 * Get a manager if he is still part of ALTEN.
	 * @param id  Manager id
	 * @return Optional manager
	 */
	public Optional<Manager> findEnabledManager(Long id) {
		return managerDAO.findByIdAndReleaseDateIsNull(id);
	}
	
	/**
	 * Get a diploma object.
	 * @param id  Diploma id
	 * @return Optional diploma
	 */
	public Optional<Diploma> findDiplomaById(Long id) {
		return diplomaDAO.findById(id);
	}
	
	/**
	 * Get all consultants.
	 * @return  List of all consultants
	 */
	public List<Consultant> findAll() {
		return consultantDAO.findAll();
	}
	
	/**
	 * Get a consultant by his id.
	 * @param id  Consultant id
	 * @return Optional consultant object
	 */
	public Optional<Consultant> findById(Long id){
		return consultantDAO.findById(id);
	}

	/**
	 * Delete a consultant.
	 * @param consultant  Consultant object.
	 */
	public void delete(Consultant consultant) {
		consultantDAO.delete(consultant);
	}

	/**
	 * Create and add a diploma to a consutlant.
	 * @param diploma  New diploma object
	 * @param consultant  Consultant object
	 * @return The new diploma
	 */
	public Diploma addDiplomaForConsultant(Diploma diploma, Consultant consultant) {
		diploma = diplomaDAO.save(diploma);
		consultant.addDiploma(diploma);
		consultantDAO.save(consultant);
		return diploma;
	}

	/**
	 * Remove a diploma from a consultant and delete the diploma from the database.
	 * @param diploma  Diploma object
	 * @param consultant  Consultant object
	 */
	public void removeDiplomaForConsultant(Diploma diploma, Consultant consultant) {
		consultant.removeDiploma(diploma);
		consultantDAO.save(consultant);
		diplomaDAO.delete(diploma);
	}

	/**
	 * Save or update a diploma object.
	 * @param diploma  Diploma object
	 * @return  Diploma object added or updated
	 */
	public Diploma saveDiploma(Diploma diploma) {
		return diplomaDAO.save(diploma);
	}
	
	/**
	 * List of all missions done by a consultant.
	 * @param consultantId  Consultant id
	 * @return  List of missions of the manager
	 */
	public List<Mission> findMissionsByConsultant(Long consultantId){
		return this.missionDAO.findByConsultantId(consultantId);
	}
}
