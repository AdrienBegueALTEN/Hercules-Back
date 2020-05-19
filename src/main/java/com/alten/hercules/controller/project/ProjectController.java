package com.alten.hercules.controller.project;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alten.hercules.controller.http.request.UpdateEntityRequest;
import com.alten.hercules.controller.project.http.request.ProjectRequest;
import com.alten.hercules.controller.project.http.request.RemoveProjectRequest;
import com.alten.hercules.dal.MissionDAL;
import com.alten.hercules.dao.project.ProjectDAO;
import com.alten.hercules.model.exception.InvalidFieldnameException;
import com.alten.hercules.model.exception.InvalidValueException;
import com.alten.hercules.model.exception.ResponseEntityException;
import com.alten.hercules.model.exception.RessourceNotFoundException;
import com.alten.hercules.model.mission.MissionSheet;
import com.alten.hercules.model.project.EProjectFieldname;
import com.alten.hercules.model.project.Project;
import com.alten.hercules.model.response.MsgResponse;
import com.alten.hercules.service.StoreImage;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/hercules/projects")
public class ProjectController {
	
	@Autowired
	ProjectDAO projectDAO;
	
	@Autowired
	MissionDAL missionDAL;
	
	@Autowired
	StoreImage storeImage;
	
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
	
	@PostMapping("/{id}/upload-picture")
	public ResponseEntity<?> uploadLogo(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
		try {
			Project proj = this.projectDAO.findById(id).orElseThrow(() -> new RessourceNotFoundException("project"));
			if(proj.getPicture()!=null) {
				this.storeImage.delete("img/proj/"+proj.getPicture());
				proj.setPicture(null);
			}
			storeImage.save(file,"project");
			proj.setPicture(file.getOriginalFilename());
			this.projectDAO.save(proj);
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
		}
	}
	
	@GetMapping("/picture/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = storeImage.loadFileAsResource(fileName,"project");

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
        	System.err.println(ex);
        } catch (NullPointerException npe) {
        	contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
