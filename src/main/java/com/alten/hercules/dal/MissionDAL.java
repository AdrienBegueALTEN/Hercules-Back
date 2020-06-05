package com.alten.hercules.dal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;

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

	public List<Mission> advancedSearch(String missionTitle, String customerName, String activitySector, String missionCity, String missionCountry, String consultantFirstName, String consultantLastName) {
		
		
		boolean queryMissionTitle = missionTitle != null && !missionTitle.equals("");
		boolean queryCustomerName = customerName != null && !customerName.equals("");
		boolean queryActivitySector = activitySector != null && !activitySector.equals("");
		boolean queryMissionCity = missionCity != null && !missionCity.equals("");
		boolean queryMissionCountry = missionCountry != null && !missionCountry.equals("");
		boolean queryConsultantFirstName = consultantFirstName != null && !consultantFirstName.equals("");
		boolean queryConsultantLastName = consultantLastName != null && !consultantLastName.equals("");
		
		
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
	    CriteriaQuery<Mission> criteriaQuery = criteriaBuilder.createQuery(Mission.class);
	    Root<Mission> missionRoot = criteriaQuery.from(Mission.class);
	    Join<Mission, Consultant> consultantJoin = missionRoot.join("consultant", JoinType.INNER);
	    Join<Mission, Customer> customerJoin = missionRoot.join("customer", JoinType.INNER);
	    Join<Mission, Customer> sheetJoin = missionRoot.join("versions", JoinType.INNER);
	    
        List<Predicate> criteriaList = new ArrayList<>();
        
        
        if(queryMissionTitle)
        {
        Predicate firstCondition = criteriaBuilder.like(sheetJoin.get("title"), "%" + missionTitle + "%");
        criteriaList.add(firstCondition);
        }
        
        
        if(queryCustomerName)
        {
        Predicate secondCondition = criteriaBuilder.like(customerJoin.get("name"), "%" + customerName + "%");
        criteriaList.add(secondCondition);
        }
        
        if(queryActivitySector)
        {
        	 Predicate thirdCondition = criteriaBuilder.like(customerJoin.get("activitySector"), "%" + activitySector + "%");
        	 criteriaList.add(thirdCondition);
        }
        
        if(queryMissionCity)
        {
        	 Predicate fourthCondition = criteriaBuilder.like(sheetJoin.get("city"), "%" + missionCity + "%");
        	 criteriaList.add(fourthCondition);
        }
        
        if(queryMissionCountry)
        {
         Predicate fifthCondition = criteriaBuilder.like(sheetJoin.get("country"), "%" + missionCountry + "%");
       	 criteriaList.add(fifthCondition);
        }
        
        if(queryConsultantFirstName)
        {
        	Predicate sixthCondition = criteriaBuilder.like(consultantJoin.get("firstname"), "%" + consultantFirstName + "%");
        	criteriaList.add(sixthCondition);
        }
        
        if(queryConsultantLastName)
        {
        	Predicate seventhCondition = criteriaBuilder.like(consultantJoin.get("lastname"), "%" + consultantLastName + "%");
        	criteriaList.add(seventhCondition);	
        }
        
        System.out.println(queryMissionTitle);
        /*
        Expression<Object> caseExpression = criteriaBuilder.selectCase()
    	    	.when(criteriaBuilder.equal(missionRoot.get("sheetStatus"), criteriaBuilder.literal("ON_WAITING")), 1)
    	    	.when(criteriaBuilder.equal(missionRoot.get("sheetStatus"), criteriaBuilder.literal("ON_GOING")), 2)
    	    	.when(criteriaBuilder.equal(missionRoot.get("sheetStatus"), criteriaBuilder.literal("VALIDATED")), 3);

    	Order temp2 = criteriaBuilder.asc(caseExpression);
    	criteriaQuery = criteriaQuery.orderBy(temp2);
        */
        criteriaQuery.where(criteriaBuilder.and(criteriaList.toArray(new Predicate[0])));
	    
	    
	    TypedQuery<Mission> query = em.createQuery(criteriaQuery);
	    /*
	    Expression<Object> caseExpression = criteriaBuilder.selectCase()
	    	.when(criteriaBuilder.equal(missionRoot.get("sheetStatus"), criteriaBuilder.literal("ON_WAITING")), 1)
	    	.when(criteriaBuilder.equal(missionRoot.get("sheetStatus"), criteriaBuilder.literal("ON_GOING")), 2)
	    	.when(criteriaBuilder.equal(missionRoot.get("sheetStatus"), criteriaBuilder.literal("VALIDATED")), 3)

	Order temp2 = criteriaBuilder.asc(caseExpression);
	criteriaQuery = criteriaQuery.orderBy(temp2);
	    */

	    return query.getResultList();
	}
	
	public List<Mission> loadAllVariables2() {
	    CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
	    CriteriaQuery<Mission> criteriaQuery = criteriaBuilder.createQuery(Mission.class);
	    Root<Mission> missionRoot = criteriaQuery.from(Mission.class);
	    
	    
	    criteriaQuery.where(criteriaBuilder.equal(missionRoot.get("id"), 1));
	    TypedQuery<Mission> query = em.createQuery(criteriaQuery);
	    //Join<Mission, Consultant> p = variableRoot.join("consultant_id", JoinType.INNER);
	    
	    //Join<Mission, Consultant> consultant = missionRoot.join("consultant", JoinType.INNER);
	    //Join<Mission, MissionSheet> missionSheet = consultant.join("versions", JoinType.INNER);
	    //Join<Mission, Customer> customer = missionSheet.join("customer", JoinType.INNER);
	    
	    
	    //Join<Mission, Consultant> consultant = missionRoot.join("consultant", JoinType.INNER);
	    //Join<Mission, MissionSheet> missionSheet = missionRoot.join("versions", JoinType.INNER);
	    //Join<Mission, Customer> customer = missionRoot.join("customer", JoinType.INNER);
	    
	    //criteriaQuery.select(missionRoot);
	    //return em.createQuery(criteriaQuery).getResultList();
	    
	    return query.getResultList();
	}

	
	public List<Mission> searchAdvancedQuery(String missionTitle, String customerName, String activitySector, String missionCity, String missionCountry, String consultantFirstName, String consultantLastName)
	{

	CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
	CriteriaQuery<Mission> criteriaQuery = criteriaBuilder.createQuery(Mission.class);
	Root<Mission> itemRoot = criteriaQuery.from(Mission.class);

	Join<Mission, MissionSheet> sheetJoin = itemRoot.join("versions",JoinType.INNER);
	Join<Mission, Consultant> consultantJoin = itemRoot.join("consultant",JoinType.INNER);
	Join<Mission, Customer> customerJoin = itemRoot.join("customer",JoinType.INNER);
	Predicate wherePredicate = criteriaBuilder.and();
	  //Root<Mission> c = criteriaQuery.from(Mission.class);

	boolean queryMissionTitle = missionTitle != null && !missionTitle.equals("");
	boolean queryCustomerName = customerName != null && !customerName.equals("");
	boolean queryActivitySector = activitySector != null && !activitySector.equals("");
	boolean queryMissionCity = missionCity != null && !missionCity.equals("");
	boolean queryMissionCountry = missionCountry != null && !missionCountry.equals("");
	boolean queryConsultantFirstName = consultantFirstName != null && !consultantFirstName.equals("");
	boolean queryConsultantLastName = consultantLastName != null && !consultantLastName.equals("");

	if(queryMissionTitle) {
	                wherePredicate = criteriaBuilder.and(wherePredicate,
	                        criteriaBuilder.like(sheetJoin.get("title"), "%" + missionTitle + "%")
	                );
	}		
					
	if(queryCustomerName) {
		wherePredicate = criteriaBuilder.and(wherePredicate,
	                        criteriaBuilder.like(customerJoin.get("name"), "%" + customerName + "%")
	                );	
	}

	if(queryActivitySector) {
		
		wherePredicate = criteriaBuilder.and(wherePredicate,
	                        criteriaBuilder.like(customerJoin.get("activity_sector"), "%" + activitySector + "%")
	                );	
	}

	if(queryMissionCity) {
		
		wherePredicate = criteriaBuilder.and(wherePredicate,
	                        criteriaBuilder.like(sheetJoin.get("city"), "%" + missionCity + "%")
	                );	
	}

	if(queryMissionCountry) {
		
		wherePredicate = criteriaBuilder.and(wherePredicate,
	                        criteriaBuilder.like(sheetJoin.get("country"), "%" + missionCountry + "%")
	                );	
	}

	if(queryConsultantFirstName)
	{
		wherePredicate = criteriaBuilder.and(wherePredicate,
	                        criteriaBuilder.like(consultantJoin.get("firstname"), "%" + consultantFirstName + "%")
	                );	
		
		
	}

	if(queryConsultantLastName)
	{
		wherePredicate = criteriaBuilder.and(wherePredicate,
	                        criteriaBuilder.like(consultantJoin.get("lastname"), "%" + consultantLastName + "%")
	                );	
		
		
	}





	criteriaQuery.where(wherePredicate);





	List<Mission> result = em.createQuery(criteriaQuery).getResultList();
	em.getTransaction().commit();
	return result;


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
