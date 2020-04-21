package com.alten.hercules.controller.diploma;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alten.hercules.dao.diploma.DiplomaDAO;
import com.alten.hercules.dao.diploma.DiplomaLocationDAO;
import com.alten.hercules.dao.diploma.DiplomaNameDAO;
import com.alten.hercules.dao.diploma.LevelDAO;
import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.diploma.Diploma;
import com.alten.hercules.model.diploma.DiplomaLocation;
import com.alten.hercules.model.diploma.DiplomaName;
import com.alten.hercules.model.diploma.Level;
import com.alten.hercules.model.request.DiplomaRequest;

@RestController
@RequestMapping("/hercules/diplomas")
public class DiplomaController {
	
	@Autowired
	private DiplomaDAO diplomaDAO;
	
	@Autowired
	private DiplomaLocationDAO diplomaLocationDAO;
	
	@Autowired
	private DiplomaNameDAO diplomaNameDAO;
	
	@Autowired
	private LevelDAO levelDAO;
	
	/**
	 * Save a new diploma in the database. Checks first if :
	 * - the level exists. If not then the level is created.
	 * - the diploma name exists. If not then the name is created.
	 * - the location of diploma exists. If not then the location is created.
	 * 
	 * NO CONTENT if request is not complete is null
	 * CONFLICT if diploma can be already found
	 * OK if created
	 * 
	 * @param diplomaRequest
	 * @return
	 */
	@PostMapping
	public ResponseEntity<?> addDiploma(@RequestBody DiplomaRequest diplomaRequest) {
		
		if(diplomaRequest.getDiplomaName().isEmpty() || 
		diplomaRequest.getGraduationCity().isEmpty() ||
		(diplomaRequest.getGraduationYear() < 1900) ||
		diplomaRequest.getLevelName().isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		
		Level level = new Level(diplomaRequest.getLevelName());
		DiplomaName diplomaName = new DiplomaName(diplomaRequest.getDiplomaName(), level);
		DiplomaLocation diplomaLocation = new DiplomaLocation(diplomaRequest.getGraduationCity());
		
		if(this.levelDAO.findByName(diplomaRequest.getLevelName())==null) 
			this.levelDAO.save(level);
		else 
			level = this.levelDAO.findByName(diplomaRequest.getLevelName());
		
		if(this.diplomaNameDAO.findByName(diplomaRequest.getDiplomaName())==null)
			this.diplomaNameDAO.save(diplomaName);
		else
			diplomaName = this.diplomaNameDAO.findByName(diplomaRequest.getDiplomaName());
		
		if(this.diplomaLocationDAO.findByCity(diplomaRequest.getDiplomaName())==null)
			this.diplomaLocationDAO.save(diplomaLocation);
		else
			diplomaLocation = this.diplomaLocationDAO.findByCity(diplomaRequest.getDiplomaName());
		
		//TODO trouver dans la table diploma un diplome avec même level, même ville, même diplomaName et même année
		// => return Conflict
		
		//Diploma diploma = new Diploma(diplomaRequest.getGraduationYear(),diplomaLocation, diplomaName);
		//this.diplomaDAO.save(diploma);
		
		
		return null;
	}
	
	@PutMapping
	public ResponseEntity<?> updateDiploma(@RequestBody DiplomaRequest diplomaRequest) {
		return null;
		
	}
	

}
