package com.alten.hercules.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alten.hercules.dao.RoleDAO;
import com.alten.hercules.dao.UserDAO;
import com.alten.hercules.model.ERole;
import com.alten.hercules.model.Role;
import com.alten.hercules.model.AppUser;
import com.alten.hercules.model.UserDetailsImpl;
import com.alten.hercules.model.request.LoginRequest;
import com.alten.hercules.model.request.RegisterRequest;
import com.alten.hercules.model.response.JwtResponse;
import com.alten.hercules.model.response.MsgResponse;
import com.alten.hercules.security.jwt.JwtUtils;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/hercules/auth")
public class UserController {
	
	@Autowired AuthenticationManager authManager;
	
	@Autowired RoleDAO roleDao;
	@Autowired UserDAO userDao;
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest request) {

		Authentication authentication = authManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = JwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();		
		List<String> roles = details.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		JwtResponse response = new JwtResponse(jwt, details.getId(), details.getUsername(), details.getFirstname(), details.getLastname(), roles);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/register")
	public ResponseEntity<Object> registerUser(@Valid @RequestBody RegisterRequest request) {

		if (userDao.existsByEmail(request.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MsgResponse("Erreur : email déjà utilisé"));
		}

		Set<String> strRoles = request.getRoles();
		Set<Role> roles = new HashSet<>();
		
		for (String strRole : strRoles) {
			try {
				ERole role = ERole.valueOf(strRole);
				try {
					roles.add(roleDao.findByName(role)
						.orElseThrow(() -> new RuntimeException("Erreur : role introuvable.")));
				} catch (RuntimeException e) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
				}
			} catch (IllegalArgumentException e) {
				return ResponseEntity
						.badRequest()
						.body(new MsgResponse("Erreur : Le rôle '" + strRole + "' n'existe pas."));				
			}
		}

		AppUser user = new AppUser(request.getEmail(), request.getPassword(), request.getFirstname(), request.getLastname(), roles);
		userDao.save(user);

		return ResponseEntity.ok(user);
	}
	 
	 @PutMapping("/auth/users")
	 public ResponseEntity<?> updateUser(@Valid @RequestBody AppUser user) {
		 
		 if (userDao.findByEmail(user.getEmail()) == null)
			 return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		 
		 userDao.save(user);
		 return new ResponseEntity<>(HttpStatus.CREATED);
	 }
	 
	 @DeleteMapping("/auth/users")
	 public ResponseEntity<?> deleteUser(@Valid @RequestBody AppUser user) {
		 
		 if (userDao.findByEmail(user.getEmail()) == null)
			 return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		 
		 userDao.delete(user);
		 return new ResponseEntity<>(HttpStatus.OK);
	 }
}
