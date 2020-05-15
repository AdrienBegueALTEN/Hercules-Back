package com.alten.hercules.controller.project;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alten.hercules.controller.http.request.UpdateEntityRequest;
import com.alten.hercules.controller.project.http.request.ProjectRequest;
import com.alten.hercules.controller.project.http.request.RemoveProjectRequest;
import com.alten.hercules.dal.MissionDAL;
import com.alten.hercules.dao.project.ProjectDAO;
import com.alten.hercules.model.exception.InvalidFieldnameException;
import com.alten.hercules.model.exception.InvalidValueException;
import com.alten.hercules.model.exception.ResponseEntityException;
import com.alten.hercules.model.exception.RessourceNotFoundException;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.mission.MissionSheet;
import com.alten.hercules.model.project.EProjectFieldname;
import com.alten.hercules.model.project.Project;
import com.alten.hercules.model.response.MsgResponse;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/hercules/projects")
public class ProjectController {
	
	@Autowired
	ProjectDAO projectDAO;
	
	@Autowired
	MissionDAL missionDAL;
	
	@GetMapping("{id}")
	public ResponseEntity<?> get(@PathVariable Long id) {
		if(this.projectDAO.findById(id).isPresent())
			return new ResponseEntity<>(this.projectDAO.findById(id).get(),HttpStatus.OK);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MsgResponse("No project with id="+id)); 
	}
	
	@PostMapping
	public ResponseEntity<?> addProject(@Valid @RequestBody ProjectRequest req ){
		try {
			MissionSheet ms = this.missionDAL.findMostRecentVersion(req.getMissionId())
					.orElseThrow(() -> new RessourceNotFoundException("project"));
			if(ms.getProjects().size()<5) {
				Project project = new Project();
				this.projectDAO.save(project);
				ms.getProjects().add(project);
				this.missionDAL.saveSheet(ms);
				return ResponseEntity.ok(project);
			}
			else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		} catch (RessourceNotFoundException e) {
			// TODO Auto-generated catch block
			return e.buildResponse();
		}
		
	}
	
	@PutMapping
	public ResponseEntity<?> updateProject(@Valid @RequestBody UpdateEntityRequest req){
		Project project;
		try {
			project = this.projectDAO.findById(req.getId())
					.orElseThrow(() -> new RessourceNotFoundException("missionsheet"));
			EProjectFieldname fieldName;
			try { fieldName = EProjectFieldname.valueOf(req.getFieldName()); }
			catch (IllegalArgumentException e) { throw new InvalidFieldnameException(); }
			switch(fieldName) {
			case title:
				project.setTitle((String)req.getValue());
				break;
			case description:
				project.setDescription((String)req.getValue());
				break;
			case beginDate:
				try {
					project.setBeginDate(new SimpleDateFormat("yyyy-MM-dd").parse((String)req.getValue()));
				} catch (ParseException e) {
					throw new InvalidValueException();
				}
				break;
			case endDate:
				try {
					project.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse((String)req.getValue()));
				} catch (ParseException e) {
					throw new InvalidValueException();
				}
				break;
			default: 
				throw new InvalidFieldnameException();
			}
			this.projectDAO.save(project);
			return ResponseEntity.ok(project);
		} catch (ResponseEntityException e) { 
			return e.buildResponse();
		} catch (ClassCastException | NullPointerException e) {
			return new InvalidValueException().buildResponse();
		}
	}
	
	@DeleteMapping
	public ResponseEntity<?> deleteProject(@RequestBody RemoveProjectRequest req) {
		try {
			MissionSheet ms = this.missionDAL.findMostRecentVersion(req.getMissionId())
					.orElseThrow(() -> new RessourceNotFoundException("missionsheet"));
			Project p = this.projectDAO.findById(req.getProjectId())
					.orElseThrow(() -> new RessourceNotFoundException("project"));
			
			if(!ms.getProjects().contains(p)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
			this.missionDAL.deleteProjetFromSheet(ms, p);
			return ResponseEntity.ok().build();
		} catch (RessourceNotFoundException e) {
			return e.buildResponse();
		}
		
	}

}
