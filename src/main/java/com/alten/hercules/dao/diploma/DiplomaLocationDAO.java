package com.alten.hercules.dao.diploma;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alten.hercules.model.diploma.DiplomaLocation;

public interface DiplomaLocationDAO extends JpaRepository<DiplomaLocation, Long> {
	public DiplomaLocation findByCity(String city);
}
