package com.alten.hercules.dao.diploma;

import org.springframework.data.jpa.repository.JpaRepository;
import com.alten.hercules.model.diploma.Diploma;

/**
 * Interface that is inherited from JpaRepository and serves to make queries for the diplomas.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public interface DiplomaDAO extends JpaRepository<Diploma, Long>{	}