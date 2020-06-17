package com.alten.hercules.dal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alten.hercules.dao.consultant.ConsultantDAO;
import com.alten.hercules.dao.customer.CustomerDAO;
import com.alten.hercules.dao.mission.MissionDAO;
import com.alten.hercules.dao.mission.MissionSheetDAO;
import com.alten.hercules.dao.project.ProjectDAO;
import com.alten.hercules.dao.skill.SkillDAO;
import com.alten.hercules.dao.user.ManagerDAO;
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
	@Autowired private ManagerDAO managerDAO;
	
	
	//The entity manager is used for the Criteria API function below
	@PersistenceContext
	EntityManager em;
	
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
	
	
	
	//The following function uses Criteria API to create a query. 
	//It is used to manage the optional parameters of the advanced search.
	public List<Mission> advancedSearchQuery(Map<String, String> criteria, Optional<Long> manager) {
		
		CriteriaBuilder builder = em.getCriteriaBuilder();
	    CriteriaQuery<Mission> query = builder.createQuery(Mission.class);
	    
	    //Gets the whole mission table
	    Root<Mission> root = query.from(Mission.class);
	    
	    //Joints are used to join tables together using objects of the root class (i.e Mission.class)
	    Join<Mission, Consultant> consultantJoin = root.join("consultant", JoinType.INNER);
	    Join<Mission, Customer> customerJoin = root.join("customer", JoinType.INNER);
	    Join<Mission, MissionSheet> sheetJoin = root.join("versions", JoinType.INNER);
	    /*Join<MissionSheet, Project> projectsJoin = sheetJoin.join("projects");
	    Join<Project, Skill> skillsJoin = projectsJoin.join("skills");*/
	    
	    //Sub query to get last version's date. 
	    Subquery<Date> subQuery = query.subquery(Date.class);
	    Root<MissionSheet> subRoot = subQuery.from(MissionSheet.class);
	    subQuery.select(builder.greatest(subRoot.get("versionDate")))
	    .where(builder.equal(root.get("id"), subRoot.get("mission")));
	    
	    //Only joins the latest version
	    sheetJoin.on(builder.equal(subQuery, sheetJoin.get("versionDate")));

	    //Contains the criteria used to create the final query
        List<Predicate> criteriaList = new ArrayList<>();
        
       
        //It checks if the title is here. If it's present, the criterion is added to the criteriaList
        String key = "title";
        if (criteria.containsKey(key) && !criteria.get(key).isBlank())
        
        	criteriaList.add(builder.like(builder.lower(sheetJoin.get("title")), ("%" + criteria.get(key) + "%").toLowerCase()));
        
        try {
            key = "customer";
	        if (criteria.containsKey(key) && !criteria.get(key).isBlank())
	        	criteriaList.add(builder.equal(customerJoin.get("id"), Long.parseLong(criteria.get(key))));
        } catch (NumberFormatException ignored) {}
	        
        try {
	        key = "consultant";
	        if (criteria.containsKey(key) && !criteria.get(key).isBlank())
	        	criteriaList.add(builder.equal(consultantJoin.get("id"), Long.parseLong(criteria.get(key))));
        } catch (NumberFormatException ignored) {}

        
        //Searches the location of the mission using country or city rows
        //Two ways lower case make the searched string case insensitive
        key = "location";
        if (criteria.containsKey(key) && !criteria.get(key).isBlank()) {
        	final String pattern = ("%" + criteria.get(key) + "%").toLowerCase();
        	final Predicate city = builder.like(builder.lower(sheetJoin.get("city")), pattern);
        	final Predicate country = builder.like(builder.lower(sheetJoin.get("country")), pattern);
        	criteriaList.add(builder.or(city, country));
        }
        
        key = "activitySector";
        if (criteria.containsKey(key) && !criteria.get(key).isBlank())
        	criteriaList.add(builder.like(builder.lower(customerJoin.get("activitySector")), ("%" + criteria.get(key) + "%").toLowerCase()));
        
        /*key = "skills";
        if (criteria.containsKey(key) && !criteria.get(key).isBlank()) {
        	String[] skills = criteria.get(key).split(",");
        	for(int i=0;i<skills.length;i++) {
        		criteriaList.add(builder.like(builder.lower(skillsJoin.get("label")), ("%" + skills[i] + "%").toLowerCase()));
        	}
        }*/
        
        
        
        Predicate AllValidatedMissions = builder.equal(root.get("sheetStatus").as(String.class), ESheetStatus.VALIDATED.name());
        
        //If the user is authenticated, the manager long exists and the query retrieves all of his missions and all validated missions of other managers
        //If the user is a guest, he only gets the validated missions
        if (manager.isPresent())
	    	criteriaList.add(builder.or(
	    			AllValidatedMissions,
	    			builder.equal(consultantJoin.get("manager"), managerDAO.getOne(manager.get()))));
        else criteriaList.add(AllValidatedMissions);
          
        
        //caseExpression watches the sheetStatus and sorts the mission by their status
        Expression<Object> caseExpression = builder.selectCase()
    	    	.when(builder.equal(root.get("sheetStatus").as(String.class), builder.literal("VALIDATED")), 3)
    	    	.when(builder.equal(root.get("sheetStatus").as(String.class), builder.literal("ON_GOING")), 2)
    	    	.when(builder.equal(root.get("sheetStatus").as(String.class), builder.literal("ON_WAITING")), 1);
     
    	query = query.orderBy(builder.asc(caseExpression));
    	
    	//Query is created using the criteriaList
    	query.where(builder.and(criteriaList.toArray(new Predicate[0])));

	    return em.createQuery(query).getResultList();
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
	
	public List<Skill> findAllSkills(){
		return this.skillDAO.findAll();
	}
}
