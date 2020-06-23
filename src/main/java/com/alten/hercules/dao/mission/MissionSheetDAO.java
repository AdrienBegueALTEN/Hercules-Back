package com.alten.hercules.dao.mission;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.alten.hercules.model.mission.MissionSheet;

/**
 * Interface that is inherited from JpaRepository and serves to make queries for the mission's sheets.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public interface MissionSheetDAO extends JpaRepository<MissionSheet, Long>{
	
	/**
	 * Query that looks for the most recent version of a mission.
	 * @param missionId ID of the mission
	 * @return The most recent version of the mission if possible.
	 */
	@Query(value = "SELECT * FROM mission_sheet WHERE mission_id=?1 AND (version_date IS NULL OR version_date = "
			+ "(SELECT MAX(version_date) FROM mission_sheet WHERE mission_id=?1))", nativeQuery = true)
	Optional<MissionSheet> findMostRecentVersion(Long missionId);}