package com.alten.hercules.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.hercules.dao.mission.MissionDAO;
import com.alten.hercules.dao.project.ProjectDAO;

@Service
public class ProjectDAL {
	@Autowired
	private ProjectDAO projectDAO;
	
	@Autowired
	private MissionDAO missionDAO;
	
	public boolean missionExists(Long id) {
		return this.missionDAO.existsById(id);
	}
}
