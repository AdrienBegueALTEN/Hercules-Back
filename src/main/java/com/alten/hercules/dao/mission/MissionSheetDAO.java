package com.alten.hercules.dao.mission;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.alten.hercules.model.mission.MissionSheet;

public interface MissionSheetDAO extends JpaRepository<MissionSheet, Long>{

	@Query(value = "SELECT * FROM mission_sheet WHERE mission_id=?1 AND version_date = "
			+ "(SELECT MAX(version_date) FROM mission_sheet WHERE mission_id=?1)", nativeQuery = true)
	Optional<MissionSheet> findMostRecentVersion(Long missionId);}