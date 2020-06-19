package com.alten.hercules.dao.consultant;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.alten.hercules.model.consultant.Consultant;

@Repository
public interface ConsultantDAO extends JpaRepository<Consultant,Integer> {

	public boolean existsByEmail(String email);
	public Iterable<Consultant> findByReleaseDateIsNull();
	public Iterable<Consultant> findByReleaseDateIsNullOrReleaseDateGreaterThan(LocalDate d);
	public Optional<Consultant> findById(Long id);
	public Optional<Consultant> findByEmail(String email);
	@Transactional
	@Modifying
	@Query(value="UPDATE consultant "
			+ "SET lastname = substring(lastname from 1 for 1) || '.', "
			+ "firstname = substring(firstname from 1 for 1) || '.', "
			+ "email = 'anonyme.anonyme@alten.com' "
			+ "WHERE id IN (SELECT id FROM consultant WHERE release_date < (now() - INTERVAL '5 year'))",
			nativeQuery = true)
	public void applyGRDP();
	
}