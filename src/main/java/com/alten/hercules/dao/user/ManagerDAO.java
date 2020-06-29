package com.alten.hercules.dao.user;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.alten.hercules.model.user.Manager;

/**
 * Interface that is inherited from JpaRepository and serves to make queries for the managers.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public interface ManagerDAO extends JpaRepository<Manager, Long>  {
	
	/**
	 * Query that looks for the managers that are active or the ones that have a release date greater than a given date.
	 * @param d Given local date
	 * @return A list of the corresponding managers.
	 */
	List<Manager> findByReleaseDateIsNullOrReleaseDateGreaterThan(LocalDate d);
}