package com.alten.hercules.dao.diploma;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alten.hercules.model.diploma.Level;

public interface LevelDAO extends JpaRepository<Level,Long>{
	
	public Level findByName(String name);
}
