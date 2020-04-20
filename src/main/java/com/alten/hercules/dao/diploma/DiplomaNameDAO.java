package com.alten.hercules.dao.diploma;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alten.hercules.model.diploma.DiplomaName;

public interface DiplomaNameDAO  extends JpaRepository<DiplomaName,Long>{
	public DiplomaName findByName(String name);
}
