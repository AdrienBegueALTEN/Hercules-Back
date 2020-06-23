package com.alten.hercules.dao.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alten.hercules.model.user.RecruitmentOfficer;

/**
 * Interface that is inherited from JpaRepository and serves to make queries for the recruitment officers.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public interface RecruitmentOfficerDAO extends JpaRepository<RecruitmentOfficer, Long>  {
	
	
	
	
}