package com.alten.hercules.controller.user;

import java.net.URI;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alten.hercules.controller.user.http.request.recruitmentOfficer.AddRecruitmentOfficerRequest;
import com.alten.hercules.controller.user.http.request.recruitmentOfficer.UpdateRecruitmentOfficerRequest;
import com.alten.hercules.dao.user.RecruitmentOfficerDAO;
import com.alten.hercules.dao.user.UserDAO;
import com.alten.hercules.model.response.MsgResponse;
import com.alten.hercules.model.user.RecruitmentOfficer;

@RestController
@CrossOrigin(origins="*")
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/hercules/recruitment-officers")
public class RecruitementOfficerController {
	
	@Autowired RecruitmentOfficerDAO recruitmentOfficerDAO;
	@Autowired UserDAO userDAO;
	
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		return ResponseEntity.ok(recruitmentOfficerDAO.findAll());
	}
	
	@PostMapping("")
	public ResponseEntity<?> addRecruitementOfficer(@Valid @RequestBody AddRecruitmentOfficerRequest request) {

		if (userDAO.existsByEmail(request.getEmail()))
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new MsgResponse("Erreur : email déjà utilisé"));
		
		RecruitmentOfficer recruitmentOfficer = request.buildUser();
		recruitmentOfficerDAO.save(recruitmentOfficer);
		
		
		URI location = URI.create(String.format("/recruitement-officers/%s", recruitmentOfficer.getId()));
		
		return ResponseEntity.created(location).build();
	}
	 
	@PutMapping("")
	public ResponseEntity<?> updateRecruitementOfficer(@Valid @RequestBody UpdateRecruitmentOfficerRequest request) {
		Optional<RecruitmentOfficer> optRecruitmentOfficer = recruitmentOfficerDAO.findById(request.getId());
		
		if (!optRecruitmentOfficer.isPresent())
			return ResponseEntity.notFound().build();
			
		RecruitmentOfficer recruitmentOfficer = optRecruitmentOfficer.get();
		
		
		if (request.getEmail() != null) {
			if (userDAO.existsByEmail(request.getEmail()) && !recruitmentOfficer.getEmail().equals(request.getEmail()))
				return ResponseEntity.status(HttpStatus.CONFLICT).build();
			recruitmentOfficer.setEmail(request.getEmail());
		}
		
		
		if (request.getFirstname() != null)
			recruitmentOfficer.setFirstname(request.getFirstname());
		
		if (request.getLastname() != null)
			recruitmentOfficer.setLastname(request.getLastname());
		

		recruitmentOfficerDAO.save(recruitmentOfficer);
		
		URI location = URI.create(String.format("/recruitment-officers/%s", recruitmentOfficer.getId()));
		
		return ResponseEntity.created(location).build();
	}
	 
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteRecruitementOfficer(@PathVariable Long id) {
		Optional<RecruitmentOfficer> optRecruitementOfficer = recruitmentOfficerDAO.findById(id);
			
		if (!optRecruitementOfficer.isPresent())
			return ResponseEntity.notFound().build();
		
		recruitmentOfficerDAO.delete(optRecruitementOfficer.get());
		return ResponseEntity.ok().build();
	 }
	
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getRecruitementOfficerById(@PathVariable Long id) {
		return ResponseEntity.ok(recruitmentOfficerDAO.findById(id));
	 }
}
