package com.alten.hercules.controller.diploma;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alten.hercules.controller.diploma.http.request.AddDiplomaRequest;
import com.alten.hercules.dal.DiplomaDAL;
import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.diploma.Diploma;
import com.alten.hercules.model.exception.RessourceNotFoundException;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/hercules/diplomas")
public class DiplomaController {
	
	@Autowired
	private DiplomaDAL dal;
	
	@PostMapping
	public ResponseEntity<?> addDiploma(@Valid @RequestBody AddDiplomaRequest request) {
		try {
			Consultant consultant = dal.findConsultantById(request.getConsultant())
					.orElseThrow(() -> new RessourceNotFoundException("Consultant"));
			Diploma diploma = request.buildDiploma();
			dal.save(diploma);
			consultant.addDiploma(diploma);
			dal.saveConsultant(consultant);
			return ResponseEntity.ok(diploma.getId());
		} catch (RessourceNotFoundException e) { return e.buildResponse(); }
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
	public ResponseEntity<?> updateDiploma(@RequestBody AddDiplomaRequest diplomaRequest) {
		/*if(diplomaRequest.getId()==null) {
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
					
		
		return new ResponseEntity<>(HttpStatus.OK);*/
		return null;
	}
	
	/**
	 * Return a diploma.
	 * 
	 * @param id
	 * @return
	 * NOT FOUND if none is found with the given id<br>
	 * OK if found
	 */
	/*@GetMapping("/{id}")
	public ResponseEntity<?> getByid(@PathVariable Long id) {
		if(!this.diplomaDAO.findById(id).isPresent())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<>(this.diplomaDAO.findById(id).get(), HttpStatus.OK);
	}
	
	@DeleteMapping
	public ResponseEntity<?> deleteDiplomaFromConsultant(@Valid @RequestBody DeleteDiplomaRequest req){
		Optional<Diploma> optDiploma = diplomaDAO.findById(req.getDiplomaId());
		if(!optDiploma.isPresent())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		Diploma d = optDiploma.get();
		
		this.diplomaDAO.deleteConsultantDiplomas(req.getConsultantId(), d.getId());
		
		this.diplomaDAO.delete(d);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}*/
	

}