package com.alten.hercules.controller.project;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alten.hercules.controller.http.request.UpdateEntityRequest;
import com.alten.hercules.dao.project.ProjectDAO;
import com.alten.hercules.model.exception.InvalidFieldnameException;
import com.alten.hercules.model.exception.InvalidValueException;
import com.alten.hercules.model.exception.ResponseEntityException;
import com.alten.hercules.model.exception.RessourceNotFoundException;
import com.alten.hercules.model.project.EProjectFieldname;
import com.alten.hercules.model.project.Project;
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
	
	@PutMapping
	public ResponseEntity<?> updateProject(@Valid @RequestBody UpdateEntityRequest req){
		Project project;
		try {
			project = this.projectDAO.findById(req.getId())
					.orElseThrow(() -> new RessourceNotFoundException("project"));
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

}
