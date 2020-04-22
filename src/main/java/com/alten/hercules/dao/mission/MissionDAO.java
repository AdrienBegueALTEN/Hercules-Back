package com.alten.hercules.dao.mission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.alten.hercules.model.mission.Mission;

public interface MissionDAO extends JpaRepository<Mission, Long>{
	
	//@Query(value="INSERT INTO mission(id, consultant_id, customer_id, team_size, version) VALUES (?1, ?2, ?3,0,0)", nativeQuery = true)
	//public Mission fastInser(long id, long consultantId, long customerId);
	
	@Query(value="SELECT * FROM mission WHERE reference=?1 AND last_update = "
			+ "(SELECT MAX(last_update) FROM mission WHERE reference=?1)", nativeQuery = true)
	public Mission lastVersionByReference(Long reference);
}
