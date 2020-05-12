package com.alten.hercules.dao.mission;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.alten.hercules.model.mission.ESheetStatus;
import com.alten.hercules.model.mission.Mission;

@Repository
public interface MissionDAO extends JpaRepository<Mission, Long> {
	
	static final String SQL_FIND_ALL = "SELECT * FROM mission WHERE "
			+ "consultant_id IN (SELECT id FROM consultant WHERE manager_id=?1) "
			+ "ORDER BY CASE sheet_status "
			+ "WHEN 'ON_WAITING' THEN 1 "
			+ "WHEN 'ON_GOING' THEN 2 "
			+ "WHEN 'VALIDATED' THEN 3 END ASC";
	
	@Query(value = SQL_FIND_ALL, nativeQuery = true)
	public List<Mission> findAllByManager(Long managerId);
	
	public List<Mission> findAllBySheetStatus(ESheetStatus status);
}