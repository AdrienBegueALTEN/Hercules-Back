package com.alten.hercules.dao.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alten.hercules.model.user.Manager;

public interface ManagerDAO extends JpaRepository<Manager, Long>  {
	Optional<Manager> findByIdAndReleaseDateIsNull(Long id);
}