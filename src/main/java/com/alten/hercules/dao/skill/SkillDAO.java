package com.alten.hercules.dao.skill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alten.hercules.model.skill.Skill;

/**
 * Interface that is inherited from JpaRepository and serves to make queries for the skills.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
@Repository
public interface SkillDAO extends JpaRepository<Skill, String>{

}
