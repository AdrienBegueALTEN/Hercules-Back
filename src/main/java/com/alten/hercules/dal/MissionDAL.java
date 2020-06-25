package com.alten.hercules.dal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
import com.alten.hercules.model.mission.ESheetStatus;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.mission.MissionSheet;
import com.alten.hercules.model.project.Project;
import com.alten.hercules.model.skill.Skill;

/**
 * Layer to access the DAL needed for the missions.
 * @author rjesson, mfoltz, abegue, jbaudot
 *
 */
@Service
public class MissionDAL {
	
	/**
	 * DAO for missions
	 */
	@Autowired private MissionDAO missionDAO;
	
	/**
	 * DAO for missions sheets
	 */
	@Autowired private MissionSheetDAO sheetDAO;
	
	/**
	 * DAO for consultants
	 */
	@Autowired private ConsultantDAO consultantDAO;
	
	/**
	 * DAO for customers
	 */
	@Autowired private CustomerDAO customerDAO;
	
	/**
	 * DAO for projects
	 */
	@Autowired private ProjectDAO projectDAO;
	
	/**
	 * DAO for skills
	 */
	@Autowired private SkillDAO skillDAO;
	
	/**
	 * DAO for managers
	 */
	@Autowired private ManagerDAO managerDAO;
	
	//The entity manager is used for the Criteria API function below
	@PersistenceContext
	EntityManager em;
	
	/**
	 * Retrieves a mission by its identifier.
	 * @param id
	 * @return the mission with the given identifier or Optional#empty() if not found.
	 */
	public Optional<Mission> findById(Long id) {
		return missionDAO.findById(id);
	}
	
	/**
	 * Retrieves a consultant by its identifier.
	 * @param id
	 * @return the consultant with the given identifier or Optional#empty() if not found.
	 */
	public Optional<Consultant> findConsultantById(Long id) {
		return consultantDAO.findById(id);
	}
	
	/**
	 * Retrieves a customer by its identifier.
	 * @param id
	 * @return the customer with the given identifier or Optional#empty() if not found.
	 */
	public Optional<Customer> findCustomerById(Long id) {
		return customerDAO.findById(id);
	}
	
	/**
	 * Saves a mission.
	 * @param mission
	 * @return the saved mission.
	 */
	public Mission save(Mission mission) {
		return missionDAO.save(mission);
	}
	
	/**
	 * Saves a mission sheet.
	 * @param sheet
	 * @return the saved mission sheet.
	 */
	public MissionSheet saveSheet(MissionSheet sheet) {
		return sheetDAO.save(sheet);
	}
	
	/**
	 * Saves a project.
	 * @param project
	 * @return the saved project.
	 */
	public Project saveProject(Project project) {
		return projectDAO.save(project);
	}
	
	/**
	 * Perform a search among the missions according to a set of criteria
	 * @param criteria The set of criteria
	 * @param manager If present filter to keep validated missions and those of the manager identified by this parameter, if not, filter to keep only validated missions.
	 * @return the list of missions which corresponds to all the criteria.
	 */
	public List<Mission> advancedSearchQuery(Map<String, String> criteria, Optional<Long> manager) {
		
		CriteriaBuilder builder = em.getCriteriaBuilder();
	    CriteriaQuery<Mission> query = builder.createQuery(Mission.class);
	    
	    //Gets the whole mission table
	    Root<Mission> root = query.from(Mission.class);
	    
	    //Joints are used to join tables together using objects of the root class (i.e Mission.class)
	    Join<Mission, Consultant> consultantJoin = root.join("consultant", JoinType.INNER);
	    Join<Mission, Customer> customerJoin = root.join("customer", JoinType.INNER);
	    Join<Mission, MissionSheet> sheetJoin = root.join("versions", JoinType.INNER);
	    
	    //Add joins towards the skills if needed only, otherwise it doesn't return results as wanted
	    Join<MissionSheet, Project> projectsJoin = null;
	    Join<Project, Skill> skillsJoin = null;
	    if (criteria.containsKey("skills") && !criteria.get("skills").isBlank()) {
		    projectsJoin = sheetJoin.join("projects", JoinType.INNER);
		    skillsJoin = projectsJoin.join("skills", JoinType.INNER);
	    }
	    
	    //Sub query to get last version's date. 
	    Subquery<Date> subQuery = query.subquery(Date.class);
	    Root<MissionSheet> subRoot = subQuery.from(MissionSheet.class);
	    subQuery.select(builder.greatest(subRoot.get("versionDate")))
	    .where(builder.equal(root.get("id"), subRoot.get("mission")));
	    
	    //Only joins the latest version
	    Predicate sheetDatePredicate = builder.or(builder.equal(subQuery, sheetJoin.get("versionDate")),
	    		builder.isNull(subQuery));
	    sheetJoin.on(sheetDatePredicate);


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
        
        //the array of skill is received as a string with skills separated with commas
        //an array of string is then created by splitting the string
        //it loop through the skills array, and create 'OR' predicates
        key = "skills";
        if (criteria.containsKey(key) && !criteria.get(key).isBlank()) {
        	String[] skills = criteria.get(key).split(",");
        	Predicate[] skillPredicates = new Predicate[skills.length];
        	for(int i=0;i<skills.length;i++) {
        		skillPredicates[i] = builder.like(builder.lower(skillsJoin.get("label")), ("%" + skills[i] + "%").toLowerCase());
        	}
        	if(skillPredicates.length>0)
        		criteriaList.add(builder.or(skillPredicates));
        }
        
        
        
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
    	
    	//The skills create duplicates. The set remove duplicates, and must be re-ordered with the sheet status
    	List<Mission> foundMissions = em.createQuery(query).getResultList();
    	Set<Mission> setMissions = new HashSet<>(foundMissions);
    	List<Mission> uniqueMissions = new ArrayList<Mission>(setMissions);
    	uniqueMissions.sort((m1,m2)->{ 
    		if(m1.getSheetStatus()==ESheetStatus.VALIDATED) return 1;
    		else if(m1.getSheetStatus()==ESheetStatus.ON_GOING) return 0;
    		else return -1;
    	});
	    return uniqueMissions;
	}
	
	/**
	 * Retrieves the most recent mission sheet of a mission.
	 * @param missionId
	 * @return the most recent mission sheet of the mission with the given identifier or Optional#empty() if not found.
	 */
	public Optional<MissionSheet> findMostRecentVersion(Long missionId) {
		return sheetDAO.findMostRecentVersion(missionId);
	}
	
	/**
	 * Retrieves all missions related to a manager's consultants.
	 * @param managerId
	 * @return the list of all missions related to the manager's consultants.
	 */
	public List<Mission> findAllByManager(Long managerId) {
		return missionDAO.findAllByManager(managerId);
	}
	
	/**
	 * Retrieves all validated missions.
	 * @return the list of all validated missions.
	 */
	public List<Mission> findAllValidated() {
		return missionDAO.findAllBySheetStatus(ESheetStatus.VALIDATED);
	}
	
	/**
	 * Deletes a project.
	 * @param project
	 */
	public void removeProject(Project project) {
		MissionSheet sheet = project.getMissionSheet();
		sheet.removeProject(project);
		sheetDAO.save(sheet);
		projectDAO.delete(project);
	}

	/**
	 * Saves a project linked to a mission sheet.
	 * @param sheet Mission sheet to which the project must be linked.
	 * @param project
	 */
	public void addProjectForSheet(MissionSheet sheet, Project project) {
		project = projectDAO.save(project);
		sheet.addProject(project);
		sheetDAO.save(sheet);
	}
	
	/**
	 * Retrieves a project by its identifier.
	 * @param id
	 * @return the project with the given identifier or Optional#empty() if not found.
	 */
	public Optional<Project> findProjectById(Long id) {
		return projectDAO.findById(id);
	}
	
	/**
	 * Delete a mission.
	 * @param mission
	 */
	public void delete(Mission mission) {
		missionDAO.delete(mission);
	}
	
	/**
	 * Retrieves all missions related to a customer.
	 * @param customerId
	 * @return the list of all missions related to the customer.
	 */
	public List<Mission> findMissionsByCustomer(Long customerId){
		return this.missionDAO.findByCustomerId(customerId);
	}
	
	/**
	 * Saves a skill linked to a project.
	 * @param project Project to which the skill must be linked.
	 * @param skill
	 */
	public void addSkillToProject(Project project, Skill skill) {
		skill = this.skillDAO.save(skill);
		project.getSkills().add(skill);
		skill.getProjects().add(project);
		this.projectDAO.save(project);
	}
	
	/**
	 * Retrieves a skill by its identifier.
	 * @param label Skill's identifier.
	 * @return the skill with the given identifier or Optional#empty() if not found.
	 */
	public Optional<Skill> findSkillByLabel(String label){
		return this.skillDAO.findById(label);
	}
	
	/**
	 * Unlink a skill from a project.
	 * @param project
	 * @param skill
	 */
	public void removeSkillFromProject(Project project, Skill skill) {
		project.getSkills().remove(skill);
		this.projectDAO.save(project);
		skill.getProjects().remove(project);
		this.skillDAO.save(skill);
		if(skill.getProjects().isEmpty())
			this.skillDAO.delete(skill);
		
	}
	
	/**
	 * Retrieves all skills.
	 * @return the list of all skills.
	 */
	public List<Skill> findAllSkills(){
		return this.skillDAO.findAll();
	}
}
