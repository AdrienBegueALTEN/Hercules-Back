package com.alten.hercules.dal;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.hercules.dao.mission.MissionDAO;
import com.alten.hercules.dao.user.ManagerDAO;
import com.alten.hercules.dao.user.UserDAO;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.user.AppUser;
import com.alten.hercules.model.user.Manager;

/**
 * Layer to access the DAL needed for the authentication.
 * @author rjesson, mfoltz, abegue, jbaudot
 *
 */
@Service
public class AuthenticationDAL {
	
	@Autowired private ManagerDAO managerDao;
	@Autowired private MissionDAO missionDao;
	@Autowired private UserDAO userDAO;
	
	/**
	 * Returns a manager by using his ID.
	 * @param id ID of the manager
	 * @return A Manager if the ID is good.
	 */
	public Optional<Manager> findManagerById(Long id) {
		return managerDao.findById(id);
	}
	
	/**
	 * Function that verifies if an email is already used by somebody in the database.
	 * @param email Email to verify
	 * @return A boolean that indicates if the email is already used.
	 */
	public boolean userExistsByEmail(String email) {
		return userDAO.existsByEmail(email);
	}
	
	/**
	 * Function that adds or updates a given manager in the database.
	 * @param manager Manager to add or update
	 * @return The same manager
	 */
	public Manager saveManager(Manager manager) {
		return managerDao.save(manager);
	}
	
	/**
	 * Function that removes the given manager in the database.
	 * @param manager
	 */
	public void deleteManager(Manager manager) {
		managerDao.delete(manager);
	}
	
	/**
	 * Returns a list of all the managers and their details.
	 * @return A list of all the managers and their details.
	 */
	public List<Manager> findAllManagers(){
		return managerDao.findAll();
	}
	
	/**
	 * Returns a mission by using its ID.
	 * @param id ID of the mission
	 * @return The mission linked to the given ID
	 */
	public Optional<Mission> findMissionById(Long id) {
		return missionDao.findById(id);
	}
	
	/**
	 * Functions that modifies the secret of a given mission.
	 * @param mission Mission to be modified
	 */
	public void changeMissionSecret(Mission mission) {
		mission.changeSecret();
		missionDao.save(mission);
	}
	
	/**
	 * Function that saves an user in the database.
	 * @param user User to be added or updated
	 * @return The same user.
	 */
	public AppUser saveUser(AppUser user) {
		return userDAO.save(user);
	}
	
	/**
	 * Returns a user by using his ID.
	 * @param id ID of the user
	 * @return The user linked to the given ID.
	 */
	public Optional<AppUser> findUserById(Long id) {
		return userDAO.findById(id);
	}

}
