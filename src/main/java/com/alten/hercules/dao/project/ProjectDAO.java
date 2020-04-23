package com.alten.hercules.dao.project;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alten.hercules.model.project.Project;

@Repository
public interface ProjectDAO extends JpaRepository<Project, Long> {
	public List<Project> findByMissionId(Long missionId);
	
}
