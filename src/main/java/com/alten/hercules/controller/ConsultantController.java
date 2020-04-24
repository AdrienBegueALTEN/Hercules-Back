package com.alten.hercules.controller;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alten.hercules.dal.ConsultantDAL;
import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.consultant.request.AddConsultantRequest;
import com.alten.hercules.model.diploma.Diploma;
import com.alten.hercules.model.exception.RessourceNotFoundException;
import com.alten.hercules.model.response.MsgResponse;
import com.alten.hercules.model.user.Manager;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/hercules/consultants")
public class ConsultantController {
	
	@Autowired
	private ConsultantDAL dal;
	
	@GetMapping("")
	public ResponseEntity<Object> getAllConsultant(@RequestParam(required = false) Boolean basic) {
		if (basic == null) basic = false;

		return ResponseEntity.ok(dal.findAllEnabled());
	}
	
	@GetMapping("/all")
	public ResponseEntity<Object> getAllConsultant() {

		return ResponseEntity.ok(dal.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Object> getById(@PathVariable Long id) {
		
		if(this.dal.findById(id)==null)
			return ResponseEntity.notFound().build();
		return ResponseEntity.ok(this.dal.findById(id));
	}
	
	@PostMapping("")
	public ResponseEntity<Object> addConsultant(@Valid @RequestBody AddConsultantRequest request) {

		if (dal.existsByEmail(request.getEmail()))
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new MsgResponse("Erreur : email déjà utilisé"));

		try {
			Manager manager = dal.findManagerById(request.getManager()).orElseThrow(() -> new RessourceNotFoundException());
			
			Set<Diploma> diplomas = new HashSet<Diploma>();
			if (request.getDiplomas() != null)
				for (Long diploma : request.getDiplomas())
					diplomas.add(dal.findDiplomaById(diploma).orElseThrow(() -> new RessourceNotFoundException()));
			
			Consultant consultant = new Consultant(request.getEmail(), request.getFirstname(), request.getLastname(), request.getExperience(), manager, diplomas);
			dal.save(consultant);
			return ResponseEntity.status(HttpStatus.CREATED).body(consultant.getId());
		} catch (RessourceNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}	
}