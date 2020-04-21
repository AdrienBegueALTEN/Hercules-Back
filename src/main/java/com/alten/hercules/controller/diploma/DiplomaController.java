package com.alten.hercules.controller.diploma;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	 * <ul>
	 * 	<li>the level exists. If not then the level is created.</li>
	 *  <li>the diploma name exists. If not then the name is created.</li>
	 * 	<li>the location of diploma exists. If not then the location is created.</li>
	 * </ul>
	 * 
	 * 
	 * @param diplomaRequest
	 * @return
	 * NO CONTENT if request is not complete<br>
	 * CONFLICT if diploma can be already found<br>
	 * OK if created
	 */
	@PostMapping
	public ResponseEntity<?> addDiploma(@RequestBody DiplomaRequest diplomaRequest) {
		
		String name = (diplomaRequest.getDiplomaName()==null)?"":diplomaRequest.getDiplomaName();
		String city = (diplomaRequest.getGraduationCity()==null)?"":diplomaRequest.getGraduationCity();
		String levelName = (diplomaRequest.getLevelName()==null)?"":diplomaRequest.getLevelName();
		String school = (diplomaRequest.getSchool()==null)?"":diplomaRequest.getSchool();
		int year = diplomaRequest.getGraduationYear();
		
		/*if(name.isEmpty() || city.isEmpty() || levelName.isEmpty( )|| school.isEmpty()) {
			return ResponseEntity.noContent().build();
		}*/
		
		if(year < 1900)
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		
		
		Level level = new Level(levelName);
		//if(this.levelDAO.findByName(levelName)==null) 
			level = this.levelDAO.save(level);
		//else 
		//	level = this.levelDAO.findByName(levelName);
		
		DiplomaName diplomaName = new DiplomaName(name, level);
		//if(this.diplomaNameDAO.findByName(name)==null)
			diplomaName = this.diplomaNameDAO.save(diplomaName);
		//else
		//	diplomaName = this.diplomaNameDAO.findByName(name);
		
		DiplomaLocation diplomaLocation = new DiplomaLocation(city, school);
		//if(this.diplomaLocationDAO.findByCity(city)==null)
			diplomaLocation = this.diplomaLocationDAO.save(diplomaLocation);
		//else
		//	diplomaLocation = this.diplomaLocationDAO.findByCity(city);
		
		if(this.diplomaDAO.findDiplome(year, city, name, levelName)!=null) 
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		
		Diploma diploma = new Diploma(diplomaRequest.getGraduationYear(),diplomaLocation, diplomaName);
		this.diplomaDAO.save(diploma);
		
		
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	/**
	 * Update a diploma (diploma, location, name or level).
	 * You must give a diplomaRequest.
	 * Ex:<br>
	 * <code>
	 *  {
	 *     "id":4, <br>
	 *     "graduationYear":2000,<br>
	 *     "graduationCity":"belf",<br>
	 *     "diplomaName":"ingé info",<br>
	 *     "levelName":"ingé3", <br>
	 *     "school":"utbm"
	 *  }
	 *  </code>
	 *  <br>
	 * The id parameter is mandatory but other are optional to update what is needed.
	 * 
	 * 
	 * @param diplomaRequest
	 * @return
	 * NOT FOUND if no diploma is found with the id<br>
	 * NO CONTENT if no id is given<br>
	 * OK after entities are updated
	 */
	@PutMapping
	public ResponseEntity<?> updateDiploma(@RequestBody DiplomaRequest diplomaRequest) {
		if(diplomaRequest.getId()==null) {
			return ResponseEntity.noContent().build();
		}
		
		long id = diplomaRequest.getId();
		String name = diplomaRequest.getDiplomaName();
		String city = diplomaRequest.getGraduationCity();
		String levelName = diplomaRequest.getLevelName();
		String school = diplomaRequest.getSchool();
		int year = diplomaRequest.getGraduationYear();
		
		Optional<Diploma> optionnalD = this.diplomaDAO.findById(id);
		if(!optionnalD.isPresent())
			return ResponseEntity.notFound().build();
		Diploma diploma = optionnalD.get();
		
		DiplomaLocation dl = diploma.getDiplomaLocation();
		DiplomaName dn = diploma.getDiplomaName();
		Level l = dn.getLevel();
		
		if(school!=null && !school.isEmpty()) {
			
			dl.setSchool(school);
			this.diplomaLocationDAO.save(dl);
		}
		
		if(city!=null && !city.isEmpty()) {
			dl.setCity(city);
			this.diplomaLocationDAO.save(dl);
		}
		
		if(levelName!=null && !levelName.isEmpty()) {
			l.setName(levelName);
			this.levelDAO.save(l);
		}
		
		if(name!=null && !name.isEmpty()) {
			dn.setName(name);
			this.diplomaNameDAO.save(dn);
		}
		
		if(year>1900) {
			diploma.setGraduationYear(year);
			this.diplomaDAO.save(diploma);
		}
					
		
		return new ResponseEntity<>(HttpStatus.OK);
		
	}
	
	@GetMapping
	public List<Diploma> getAll(){
		return this.diplomaDAO.findAll();
	}
	
	/**
	 * Return a diploma.
	 * 
	 * @param id
	 * @return
	 * NOT FOUND if none is found with the given id<br>
	 * OK if found
	 */
	@GetMapping("/{id}")
	public ResponseEntity<?> getByid(@PathVariable Long id) {
		if(!this.diplomaDAO.findById(id).isPresent())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<>(this.diplomaDAO.findById(id).get(), HttpStatus.OK);
	}
	

}
