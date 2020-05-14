package com.alten.hercules.dao.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.alten.hercules.model.project.Project;

public interface ProjectDAO extends JpaRepository<Project, Long>{
	@Query(value="INSERT INTO mission_sheet_projects VALUES (?1, ?2);", nativeQuery = true)
	public void insertMissionSheetProjects(Long msId, Long pId);
}
