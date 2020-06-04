package com.alten.hercules.dao.mission;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.alten.hercules.model.mission.ESheetStatus;
import com.alten.hercules.model.mission.Mission;

@Repository
public interface MissionDAO extends JpaRepository<Mission, Long> {
	
	static final String ADVANCED_SEARCH_QUERY = "SELECT mission.id, mission.sheet_status, mission.consultant_id, mission.customer_id," + 
			"mission_sheet.id, mission_sheet.city, mission_sheet.comment, mission_sheet.consultant_role, mission_sheet.consultant_start_xp, mission_sheet.contract_type, mission_sheet.country, mission_sheet.description, mission_sheet.team_size, mission_sheet.title," + 
			"consultant.id, consultant.email, consultant.experience, consultant.firstname, consultant.lastname," + 
			"customer.activity_sector, customer.description, customer.name," + 
			"app_user.email, app_user.firstname, app_user.lastname" + 
			"FROM mission INNER JOIN mission_sheet ON mission.id = mission_sheet.mission_id" + 
			"INNER JOIN consultant ON mission.consultant_id = consultant.id" + 
			"INNER JOIN customer ON mission.customer_id = customer.id" + 
			"INNER JOIN app_user ON consultant.manager_id=app_user.id" + 
			"AND (?1 is null or mission_sheet.title=?1)" + 
			"AND (?2 is null or customer.name=?2)" + 
			"AND (?3 is null or customer.activity_sector=?3)" + 
			"AND (?4 is null or mission_sheet.city=?4)" + 
			"AND (?5 is null or mission_sheet.country=?5)" + 
			"AND (?6 is null or consultant.firstname=?6)" + 
			"AND (?7 is null or consultant.lastname=?7)" + 
			"AND (?8 is null or app_user.firstname=?8)" + 
			"AND (?9 is null or app_user.lastname=?9)" + 
			"AND consultant_id IN (SELECT id FROM consultant WHERE manager_id=1 OR sheet_status='VALIDATED')" + 
			"AND (version_date IS NULL OR version_date = (SELECT MAX(version_date) FROM mission_sheet WHERE mission_sheet.mission_id=mission.id))" + 
			"ORDER BY CASE sheet_status" + 
			"WHEN 'ON_WAITING' THEN 1" + 
			"WHEN 'ON_GOING' THEN 2" + 
			"WHEN 'VALIDATED' THEN 3 END ASC";
	
	static final String SQL_FIND_ALL = "SELECT * FROM mission WHERE "
			+ "consultant_id IN (SELECT id FROM consultant WHERE manager_id=?1 OR sheet_status='VALIDATED') "
			+ "ORDER BY CASE sheet_status "
			+ "WHEN 'ON_WAITING' THEN 1 "
			+ "WHEN 'ON_GOING' THEN 2 "
			+ "WHEN 'VALIDATED' THEN 3 END ASC";
	
	@Query(value = SQL_FIND_ALL, nativeQuery = true)
	public List<Mission> findAllByManager(Long managerId);
	
	public List<Mission> findAllBySheetStatus(ESheetStatus status);
	
	public List<Mission> findByCustomerId(Long customerId);
	
	public List<Mission> findByConsultantId(Long consultantId);
	
	@Query(value = ADVANCED_SEARCH_QUERY, nativeQuery = true)
	public List<Mission> getSearchResults(String missionTitle, String customerName, String activitySector, String missionCity, String missionCountry, String consultantFirstName, String consultantLastName, String managerFirstName, String managerLastName);
}