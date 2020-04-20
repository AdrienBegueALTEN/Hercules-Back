package com.alten.hercules.controller;

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

import com.alten.hercules.dao.UserDAO;
import com.alten.hercules.model.request.user.RegisterUserRequest;
import com.alten.hercules.model.request.user.UpdateUserRequest;
import com.alten.hercules.model.response.MsgResponse;
import com.alten.hercules.model.user.AppUser;
import com.alten.hercules.model.user.ERole;
import com.alten.hercules.model.user.Manager;
import com.alten.hercules.model.user.RecruitementOfficer;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/hercules/users")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {
	
	@Autowired UserDAO dao;
	

	@PostMapping("")
	public ResponseEntity<Object> registerUser(@Valid @RequestBody RegisterUserRequest request) {

		if (dao.existsByEmail(request.getEmail()))
			return ResponseEntity.badRequest().body(new MsgResponse("Erreur : email déjà utilisé"));
		
		try {
			AppUser user;
			boolean isAdmin = false;
			
			switch (ERole.valueOf(request.getRole())) {
			case ADMIN:
				isAdmin = true;
			case MANAGER:
				user = new Manager(request.getEmail(), request.getPassword(), request.getFirstname(), request.getLastname(), isAdmin);
				break;
			case RECRUITEMENT_OFFICER:
				user = new RecruitementOfficer(request.getEmail(), request.getPassword(), request.getFirstname(), request.getLastname());
				break;
			default:
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
			
			dao.save(user);
			URI location = URI.create(String.format("/persons/%s", user.getId()));
			
			return ResponseEntity.created(location).build();
			
		} catch (IllegalArgumentException e) {
			return ResponseEntity
					.badRequest()
					.body(new MsgResponse("Erreur : Le rôle '" + request.getRole() + "' n'existe pas."));				
		}
	}
	 
	 @PutMapping("")
	 public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserRequest request) {
			Optional<AppUser> optUser = dao.findById(request.getId());
			
			if (!optUser.isPresent())
				return ResponseEntity.notFound().build();
				
			AppUser user = optUser.get();
			
			if (request.getEmail() != null) {
				if (dao.findByEmail(request.getEmail()) != null)
					return ResponseEntity.status(HttpStatus.CONFLICT).build();
				user.setEmail(request.getEmail());
			}
			
			if (request.getPassword() != null)
				user.setPassword(request.getPassword());
			
			if (request.getFirstname() != null)
				user.setEmail(request.getFirstname());
			
			if (request.getLastname() != null)
				user.setLastname(request.getLastname());
			
			if (request.getReleaseDate() != null)
				user.setReleaseDate(request.getReleaseDate());

			if (request.getRole() != null) {
				if (!(user instanceof Manager))
					return ResponseEntity.badRequest().build();
				Manager manager = ((Manager) user);
				manager.setAdmin(request.getRole().equals("ADMIN"));
				dao.save(manager);
			} else dao.save(user);
			

			URI location = URI.create(String.format("/persons/%s", user.getId()));
			
			return ResponseEntity.created(location).build();
	 }
	 
	 @DeleteMapping("/{id}")
	 public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		Optional<AppUser> user = dao.findById(id);
		
		if (user.isPresent()) {
			if (user.get() instanceof Manager) {
				Manager manager = (Manager)user.get();
				//TODO Retour si le manager est lié à au moins un consultant consultants
			}
			dao.delete(user.get());
			return ResponseEntity.ok().build();
		}
		 return ResponseEntity.notFound().build();
	 }
}
