package com.alten.hercules.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alten.hercules.model.ERole;
import com.alten.hercules.model.Role;

public interface RoleDAO extends JpaRepository<Role, Long> {
	
	Optional<Role> findByName(ERole name);
	
}
