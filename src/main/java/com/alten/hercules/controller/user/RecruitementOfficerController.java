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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alten.hercules.controller.user.http.request.recruitementOfficer.AddRecruitementOfficerRequest;
import com.alten.hercules.controller.user.http.request.recruitementOfficer.UpdateRecruitementOfficerRequest;
import com.alten.hercules.dao.user.RecruitementOfficerDAO;
import com.alten.hercules.dao.user.UserDAO;
import com.alten.hercules.model.response.MsgResponse;
import com.alten.hercules.model.user.RecruitementOfficer;

@RestController
@CrossOrigin(origins="*")
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/hercules/recruitementOfficer")
public class RecruitementOfficerController {
	
	@Autowired RecruitementOfficerDAO recruitementOfficerDAO;
	@Autowired UserDAO userDAO;
	
	@PostMapping("")
	public ResponseEntity<Object> addRecruitementOfficer(@Valid @RequestBody AddRecruitementOfficerRequest request) {

		if (userDAO.existsByEmail(request.getEmail()))
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new MsgResponse("Erreur : email déjà utilisé"));
		
		RecruitementOfficer recruitementOfficer = request.buildUser();
		recruitementOfficerDAO.save(recruitementOfficer);
		URI location = URI.create(String.format("/recruitementOfficer/%s", recruitementOfficer.getId()));
		
		return ResponseEntity.created(location).build();
	}
	 
	@PutMapping("")
	public ResponseEntity<?> updateRecruitementOfficer(@Valid @RequestBody UpdateRecruitementOfficerRequest request) {
		Optional<RecruitementOfficer> optRecruitementOfficer = recruitementOfficerDAO.findById(request.getId());
		
		if (!optRecruitementOfficer.isPresent())
			return ResponseEntity.notFound().build();
			
		RecruitementOfficer recruitementOfficer = optRecruitementOfficer.get();
		
		if (request.getEmail() != null) {
			if (userDAO.existsByEmail(request.getEmail()))
				return ResponseEntity.status(HttpStatus.CONFLICT).build();
			recruitementOfficer.setEmail(request.getEmail());
		}
		
		if (request.getPassword() != null)
			recruitementOfficer.setPassword(request.getPassword());
		
		if (request.getFirstname() != null)
			recruitementOfficer.setEmail(request.getFirstname());
		
		if (request.getLastname() != null)
			recruitementOfficer.setLastname(request.getLastname());
		
		if (request.getReleaseDate() != null)
			recruitementOfficer.setReleaseDate(request.getReleaseDate());
		
		URI location = URI.create(String.format("/recruitementOfficer/%s", recruitementOfficer.getId()));
		
		return ResponseEntity.created(location).build();
	}
	 
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteRecruitementOfficer(@PathVariable Long id) {
		Optional<RecruitementOfficer> optRecruitementOfficer = recruitementOfficerDAO.findById(id);
			
		if (!optRecruitementOfficer.isPresent())
			return ResponseEntity.notFound().build();
		
		recruitementOfficerDAO.delete(optRecruitementOfficer.get());
		return ResponseEntity.ok().build();
	 }
}
