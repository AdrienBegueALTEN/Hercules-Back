package com.alten.hercules.controller.auth;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alten.hercules.controller.auth.http.request.ChangePasswordRequest;
import com.alten.hercules.controller.auth.http.request.LoginRequest;
import com.alten.hercules.dao.user.UserDAO;
import com.alten.hercules.model.response.JWTResponse;
import com.alten.hercules.model.user.AppUser;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/hercules/auth")
public class AuthController {
	
	@Autowired private AuthenticationManager authManager;
	@Autowired private UserDAO userDAO;
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest request) {
		Authentication authentication = authManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return ResponseEntity.ok(new JWTResponse((AppUser)authentication.getPrincipal()));
	}
	
	@PreAuthorize("hasAuthority('CREATE_PASSWORD')")
	@PutMapping("/create-password-anonymous")
	public ResponseEntity<?> createPassword(@RequestBody String password) {
		AppUser user = (AppUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (user.getPassword() != null)
			ResponseEntity.status(HttpStatus.CONFLICT).body("Password already definded.");
		user.setPassword(password);
		user = userDAO.save(user);
		return ResponseEntity.ok(new JWTResponse(user));
	}
	
	@PutMapping("/change-password")
	public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
		AppUser user = (AppUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		String providedCurrentPassword = request.getCurrentPassword();
		if (!new BCryptPasswordEncoder().matches(providedCurrentPassword, user.getPassword()))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("The provided password doesn't match with the current password.");
		user.setPassword(request.getNewPassword());
		user = userDAO.save(user);
		return ResponseEntity.ok(null);
	}
}
