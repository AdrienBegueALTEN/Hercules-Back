package com.alten.hercules.dao.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.alten.hercules.model.user.AppUser;

/**
 * Interface that is inherited from JpaRepository and serves to make queries for the users.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
@Repository
public interface UserDAO extends JpaRepository<AppUser, Long>  {
	
	/**
	 * Query that checks if a user has the given email.
	 * @param email Given email
	 * @return A boolean that indicates if a user owns the given email.
	 */
	boolean existsByEmail(String email);
	
	/**
	 * Query that looks for an user with the given email.
	 * @param email Given email
	 * @return A corresponding user of possible;
	 */
	Optional<AppUser> findByEmail(String email);
	
	/**
	 * Query that applies the GRPD to an user by making his fields anonymous.
	 */
	@Transactional
	@Modifying
	@Query(value="UPDATE app_user "
			+ "SET lastname = substring(lastname from 1 for 1) || '.', "
			+ "firstname = substring(firstname from 1 for 1) || '.', "
			+ "email = 'anonyme.anonyme@alten.com' "
			+ "WHERE id IN (SELECT id FROM app_user WHERE release_date < (now() - INTERVAL '5 year'))",
			nativeQuery = true)
	public void applyGRDP();
	
}
