package com.alten.hercules.controller.user;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alten.hercules.controller.user.http.request.manager.AddManagerRequest;
import com.alten.hercules.controller.user.http.request.manager.UpdateManagerRequest;
import com.alten.hercules.dal.AuthenticationDAL;
import com.alten.hercules.model.response.MsgResponse;
import com.alten.hercules.model.user.Manager;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/hercules/managers")
public class ManagerController {
	
	@Autowired private AuthenticationDAL dal;
	
	@GetMapping("/{id}")
	public ResponseEntity<Object> getManager(@PathVariable Long id) {
		Optional<Manager> manager = dal.findManagerById(id);
		
		if (!manager.isPresent())
			return ResponseEntity.notFound().build();
		
		return ResponseEntity.ok(manager.get());
	}
	
	@GetMapping
	public ResponseEntity<Object> getAll(@RequestParam boolean basic) {
		List<Manager> managers = dal.findAllManagers();
		return ResponseEntity.ok(managers);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("")
	public ResponseEntity<Object> addManager(@Valid @RequestBody AddManagerRequest request) {
		if (dal.userExistsByEmail(request.getEmail()))
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new MsgResponse("Erreur : email déjà utilisé"));
		
		Manager manager = request.buildUser();
		dal.saveManager(manager);
		URI location = URI.create(String.format("/manager/%s", manager.getId()));
			
		return ResponseEntity.created(location).build();
	}
	 
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("")
	public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateManagerRequest request) {
		Optional<Manager> optManager = dal.findManagerById(request.getId());
		
		if (!optManager.isPresent())
			return ResponseEntity.notFound().build();
			
		Manager manager = optManager.get();
		
		if (request.getEmail() != null) {
			if (dal.userExistsByEmail(request.getEmail()))
				return ResponseEntity.status(HttpStatus.CONFLICT).build();
			manager.setEmail(request.getEmail());
		}
		
		if (request.getPassword() != null)
			manager.setPassword(request.getPassword());
		
		if (request.getFirstname() != null)
			manager.setEmail(request.getFirstname());
		
		if (request.getLastname() != null)
			manager.setLastname(request.getLastname());
		
		if (request.getReleaseDate() != null)
			manager.setReleaseDate(request.getReleaseDate());

			manager.setAdmin(request.isAdmin());
		
		URI location = URI.create(String.format("/managers/%s", manager.getId()));
		
		return ResponseEntity.created(location).build();
	}
	 
	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		Optional<Manager> optManager = dal.findManagerById(id);
		
		if (optManager.isPresent()) {
			Manager manager = optManager.get();
			
			if (!manager.getConsultants().isEmpty())
				return ResponseEntity.status(HttpStatus.CONFLICT).build();
	
			dal.deleteManager(manager);
			return ResponseEntity.ok().build();
		}
		 return ResponseEntity.notFound().build();
	}
}