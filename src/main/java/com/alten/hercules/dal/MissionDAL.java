package com.alten.hercules.dal;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alten.hercules.dao.consultant.ConsultantDAO;
import com.alten.hercules.dao.customer.CustomerDAO;
import com.alten.hercules.dao.mission.MissionDAO;
import com.alten.hercules.dao.mission.MissionSheetDAO;
import com.alten.hercules.dao.project.ProjectDAO;
import com.alten.hercules.dao.skill.SkillDAO;
import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.exception.ResourceNotFoundException;
import com.alten.hercules.model.mission.ESheetStatus;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.mission.MissionSheet;
import com.alten.hercules.model.project.Project;
import com.alten.hercules.model.skill.Skill;

@Service
public class MissionDAL {
	
	@Autowired private MissionDAO missionDAO;
	@Autowired private MissionSheetDAO sheetDAO;
	@Autowired private ConsultantDAO consultantDAO;
	@Autowired private CustomerDAO customerDAO;
	@Autowired private ProjectDAO projectDAO;
	@Autowired private SkillDAO skillDAO;
	
	public Optional<Mission> findById(Long id) {
		return missionDAO.findById(id);
	}
	
	public Optional<Consultant> findConsultantById(Long id) {
		return consultantDAO.findById(id);
	}
	
	public Optional<Customer> findCustomerById(Long id) {
		return customerDAO.findById(id);
	}
	
	public Mission save(Mission mission) {
		return missionDAO.save(mission);
	}
	
	public MissionSheet saveSheet(MissionSheet sheet) {
		return sheetDAO.save(sheet);
	}
	
	public void saveProject(Project project) {
		projectDAO.save(project);
	}


	public Optional<MissionSheet> findMostRecentVersion(Long missionId) {
		return sheetDAO.findMostRecentVersion(missionId);
	}
	
	public List<Mission> findAllByManager(Long managerId) {
		return missionDAO.findAllByManager(managerId);
	}
	
	public List<Mission> findAllValidated() {
		return missionDAO.findAllBySheetStatus(ESheetStatus.VALIDATED);
	}

	public void changeMissionSecret(Mission mission) {
		mission.changeSecret();
		missionDAO.save(mission);
	}
	
	public void removeProject(Project project) throws ResourceNotFoundException {
		MissionSheet sheet = project.getMissionSheet();
		sheet.removeProject(project);
		sheetDAO.save(sheet);
		projectDAO.delete(project);
	}

	public void addProjectForSheet(MissionSheet sheet, Project project) throws ResourceNotFoundException {
		project = projectDAO.save(project);
		sheet.addProject(project);
		sheetDAO.save(sheet);
	}
	
	public Optional<Project> findProjectById(Long id) {
		return projectDAO.findById(id);
	}
	
	
	public void delete(Mission mission) {
		missionDAO.delete(mission);
	}
	
	public List<Mission> findMissionsByCustomer(Long customerId){
		return this.missionDAO.findByCustomerId(customerId);
	}
	
	public void addSkillToProject(Project p, Skill s) {
		s=this.skillDAO.save(s);
		p.getSkills().add(s);
		s.getProjects().add(p);
		this.projectDAO.save(p);
	}
	
	public Optional<Skill> findSkillByLabel(String label){
		return this.skillDAO.findById(label);
	}
	
	public void removeSkillFromProject(Project p, Skill s) {
		p.getSkills().remove(s);
		this.projectDAO.save(p);
		s.getProjects().remove(p);
		this.skillDAO.save(s);
		if(s.getProjects().isEmpty())
			this.skillDAO.delete(s);
		
	}

}
