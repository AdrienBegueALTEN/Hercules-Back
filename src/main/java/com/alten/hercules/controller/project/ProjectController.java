package com.alten.hercules.controller.project;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alten.hercules.dao.project.ProjectDAO;
import com.alten.hercules.model.project.Project;
import com.alten.hercules.model.project.request.ProjectRequest;
import com.alten.hercules.model.response.MsgResponse;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/hercules/projects")
public class ProjectController {
	
	@Autowired
	ProjectDAO projectDAO;
	
	
	@GetMapping("{id}")
	public ResponseEntity<?> get(@PathVariable Long id) {
		if(this.projectDAO.findById(id).isPresent())
			return new ResponseEntity<>(this.projectDAO.findById(id).get(),HttpStatus.OK);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MsgResponse("No project with id="+id)); 
	}
	
	@PostMapping
	public ResponseEntity<?> addProject(){
		Project project = new Project();
		this.projectDAO.save(project);
		return ResponseEntity.ok(project);
	}

}
