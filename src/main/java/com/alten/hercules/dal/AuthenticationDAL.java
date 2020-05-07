package com.alten.hercules.dal;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.hercules.dao.mission.MissionDAO;
import com.alten.hercules.dao.user.ManagerDAO;
import com.alten.hercules.dao.user.UserDAO;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.user.Manager;

@Service
public class AuthenticationDAL {
	
	@Autowired private ManagerDAO managerDao;
	@Autowired private MissionDAO missionDao;
	@Autowired private UserDAO userDao;
	
	public Optional<Manager> findManagerById(Long id) {
		return managerDao.findById(id);
	}
	
	public boolean userExistsByEmail(String email) {
		return userDao.existsByEmail(email);
	}
	
	public void saveManager(Manager manager) {
		managerDao.save(manager);
	}

	public void deleteManager(Manager manager) {
		managerDao.delete(manager);
	}
	
	public List<Manager> findAllManagers(){
		return managerDao.findAll();
	}
	
	public Optional<Mission> findMissionById(Long id) {
		return missionDao.findById(id);
	}

	public void changeMissionSecret(Mission mission) {
		mission.changeSecret();
		missionDao.save(mission);
	}

}
