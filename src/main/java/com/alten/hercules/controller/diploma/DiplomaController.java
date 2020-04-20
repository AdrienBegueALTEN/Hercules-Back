package com.alten.hercules.controller.diploma;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
		
		String name = diplomaRequest.getDiplomaName();
		String city = diplomaRequest.getGraduationCity();
		String levelName = diplomaRequest.getLevelName();
		int year = diplomaRequest.getGraduationYear();
		
		if(name.isEmpty() || city.isEmpty() || (year < 1900) || levelName.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		
		
		Level level = new Level(levelName);
		if(this.levelDAO.findByName(levelName)==null) 
			level = this.levelDAO.save(level);
		else 
			level = this.levelDAO.findByName(levelName);
		System.out.println(level);
		
		DiplomaName diplomaName = new DiplomaName(name, level);
		if(this.diplomaNameDAO.findByName(name)==null)
			diplomaName = this.diplomaNameDAO.save(diplomaName);
		else
			diplomaName = this.diplomaNameDAO.findByName(name);
		
		
		DiplomaLocation diplomaLocation = new DiplomaLocation(city);
		if(this.diplomaLocationDAO.findByCity(city)==null)
			diplomaLocation = this.diplomaLocationDAO.save(diplomaLocation);
		else
			diplomaLocation = this.diplomaLocationDAO.findByCity(city);
		
		//TODO trouver dans la table diploma un diplome avec même level, même ville, même diplomaName et même année
		if(this.diplomaDAO.findDiplome(year, city, name, levelName)!=null) 
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		
		Diploma diploma = new Diploma(diplomaRequest.getGraduationYear(),diplomaLocation, diplomaName);
		this.diplomaDAO.save(diploma);
		
		
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@PutMapping
	public ResponseEntity<?> updateDiploma(@RequestBody DiplomaRequest diplomaRequest) {
		return null;
		
	}
	
	@GetMapping
	public List<Diploma> getAll(){
		return this.diplomaDAO.findAll();
	}
	

}
