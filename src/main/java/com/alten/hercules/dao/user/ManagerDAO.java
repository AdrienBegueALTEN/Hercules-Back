package com.alten.hercules.dao.user;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.user.Manager;

public interface ManagerDAO extends JpaRepository<Manager, Long>  {
	Optional<Manager> findByIdAndReleaseDateIsNull(Long id);
	List<Manager> findByReleaseDateIsNull();
	List<Manager> findByReleaseDateIsNullOrReleaseDateGreaterThan(LocalDate d);
}