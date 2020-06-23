package com.alten.hercules.dao.consultant;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.alten.hercules.model.consultant.Consultant;

/**
 * Interface that is inherited from JpaRepositiry and serves to make queries for the consultants.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
@Repository
public interface ConsultantDAO extends JpaRepository<Consultant,Integer> {
	
	/**
	 * Query that checks if a consultant possesses the given email.
	 * @param email Email to verify
	 * @return A boolean that indicates if a consultant has this email.
	 */
	public boolean existsByEmail(String email);
	
	/**
	 * Query that returns a list of the active consultants.
	 * @return A list of the active consultants.
	 */
	public Iterable<Consultant> findByReleaseDateIsNull();
	
	/**
	 * Query that returns a list of the active consultants and the ones that have a release date greater than a given date.
	 * @param d Given local date
	 * @return A list of the active consultants and the ones that have a release date greater than a given date.
	 */
	public Iterable<Consultant> findByReleaseDateIsNullOrReleaseDateGreaterThan(LocalDate d);
	
	/**
	 * Query that looks for a consultant by using a given ID.
	 * @param id Given ID
	 * @return A consultant linked to the ID if possible.
	 */
	public Optional<Consultant> findById(Long id);
	
	/**
	 * Query that looks for a consultant by using a given email.
	 * @param email Given email
	 * @return A consultant linked to the email if possible.
	 */
	public Optional<Consultant> findByEmail(String email);
	
	/**
	 * Query that applies the GRPD to a consultant and makes his information anonymous.
	 */
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