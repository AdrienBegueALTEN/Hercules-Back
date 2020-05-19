package com.alten.hercules.dao.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.alten.hercules.model.user.AppUser;

@Repository
public interface UserDAO extends JpaRepository<AppUser, Long>  {
	
	boolean existsByEmail(String email);
	Optional<AppUser> findByEmail(String email);
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
