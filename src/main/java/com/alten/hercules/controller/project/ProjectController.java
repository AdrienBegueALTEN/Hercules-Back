package com.alten.hercules.controller.project;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alten.hercules.model.project.Project;
import com.alten.hercules.model.project.request.ProjectRequest;
import com.alten.hercules.model.response.MsgResponse;

@RestController
@RequestMapping("/hercules/projects")
public class ProjectController {
	
	/*@Autowired
	ProjectDAO projectDAO;
	
	@Autowired
	//ProjectDAL projectDAL;
	
	@GetMapping
	public List<Project> getAll() {
		return this.projectDAO.findAll();
	}
	
	@GetMapping("{id}")
	public ResponseEntity<?> getAll(@PathVariable Long id) {
		if(this.projectDAO.findById(id).isPresent())
			return new ResponseEntity<>(this.projectDAO.findById(id).get(),HttpStatus.OK);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MsgResponse("No project with id="+id)); 
	}
	
	@PostMapping
	public ResponseEntity<?> addProject(@RequestBody ProjectRequest req){
		if(req.getMissionId()==null)
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new MsgResponse("Mission id is not present.")); 
		
		if(this.projectDAL.missionExists(req.getMissionId()))
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MsgResponse("Mission with id="+req.getMissionId()+" is not found.")); 
		Project project = new Project();
		
		if(req.getBeginDate()!=null)
			project.setBeginDate(req.getBeginDate());
		
		if(req.getEndDate()!=null)
			project.setEndDate(req.getEndDate());
		
		if(req.getDescription()!=null)
			project.setDescription(req.getDescription());
		
		this.projectDAO.save(project);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}*/

}
