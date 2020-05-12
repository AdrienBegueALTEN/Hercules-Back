package com.alten.hercules.dao.project;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alten.hercules.model.project.Project;

public interface ProjectDAO extends JpaRepository<Project, Long>{

}
