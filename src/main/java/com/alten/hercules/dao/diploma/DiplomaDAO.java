package com.alten.hercules.dao.diploma;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.alten.hercules.model.diploma.Diploma;

public interface DiplomaDAO extends JpaRepository<Diploma, Long>{
	
	@Query(value = "SELECT * "
			+ "FROM diploma d, diploma_location loc, diploma_name n, level l "
			+ "WHERE d.diploma_name_id = n.id "
			+ "AND d.diploma_location_id = loc.id "
			+ "AND n.level_id = l.id "
			+ "AND d.graduation_year = ?1 "
			+ "AND loc.city = ?2 "
			+ "AND n.name = ?3 "
			+ "AND l.name = ?4", nativeQuery = true)
	public Diploma findDiplome(int year, String city, String name, String level);
	
}
