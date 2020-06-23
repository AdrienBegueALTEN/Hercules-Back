package com.alten.hercules.dao.mission;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.alten.hercules.model.mission.ESheetStatus;
import com.alten.hercules.model.mission.Mission;

/**
 * Interface that is inherited from JpaRepository and serves tom ake queries for the missions.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
@Repository
public interface MissionDAO extends JpaRepository<Mission, Long> {
	
	static final String SQL_FIND_ALL = "SELECT * FROM mission WHERE "
			+ "consultant_id IN (SELECT id FROM consultant WHERE manager_id=?1) OR sheet_status='VALIDATED'"
			+ "ORDER BY CASE sheet_status "
			+ "WHEN 'ON_WAITING' THEN 1 "
			+ "WHEN 'ON_GOING' THEN 2 "
			+ "WHEN 'VALIDATED' THEN 3 END ASC";
	
	@Query(value = SQL_FIND_ALL, nativeQuery = true)
	
	/**
	 * Query that returns a list of the mission that are linked to a specific manager.
	 * @param managerId ID of the manager
	 * @return A list of the mission that are linked to the manager.
	 */
	public List<Mission> findAllByManager(Long managerId);
	
	/**
	 * Query that returns a list of the mission that have a specific status.
	 * @param status Status of the mission
	 * @return A list of the mission that have a specific status.
	 */
	public List<Mission> findAllBySheetStatus(ESheetStatus status);
	
	/**
	 * Query that returns a list of the mission that are linked to a specific customer.
	 * @param customerId ID of the customer
	 * @return A list of the mission that are linked to the customer.
	 */
	public List<Mission> findByCustomerId(Long customerId);
	
	/**
	 * Query that returns a list of the mission that are linked to a specific manager.
	 * @param consultantId ID of the manager
	 * @return A list of the mission that are linked to the consultant.
	 */
	public List<Mission> findByConsultantId(Long consultantId);
}