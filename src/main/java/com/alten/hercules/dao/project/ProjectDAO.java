package com.alten.hercules.dao.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.alten.hercules.model.project.Project;

/**
 * Interface that is inherited from JpaRepository and serves to make queries for the projects.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public interface ProjectDAO extends JpaRepository<Project, Long>{
	
	/**
	 * Query that puts a project into the sheet of mission.
	 * @param msId ID of the mission
	 * @param pId ID of the project
	 */
	@Query(value="INSERT INTO mission_sheet_projects VALUES (?1, ?2);", nativeQuery = true)
	public void insertMissionSheetProjects(Long msId, Long pId);
}
