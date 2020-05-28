package com.alten.hercules.dao.skill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alten.hercules.model.skill.Skill;

@Repository
public interface SkillDAO extends JpaRepository<Skill, String>{

}
