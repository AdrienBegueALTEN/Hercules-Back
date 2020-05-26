package com.alten.hercules.controller.auth;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alten.hercules.controller.auth.http.request.LoginRequest;
import com.alten.hercules.dal.AuthenticationDAL;
import com.alten.hercules.model.response.JWTResponse;
import com.alten.hercules.model.user.AppUser;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/hercules/auth")
public class AuthController {
	
	@Autowired private AuthenticationManager authManager;
	@Autowired private AuthenticationDAL dal;
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest request) {
		Authentication authentication = authManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return ResponseEntity.ok(new JWTResponse((AppUser)authentication.getPrincipal()));
	}
	
	private void changePassword(AppUser user, String providedPassword, String newPassword) {
		String userPassword = user.getPassword();
		providedPassword = new BCryptPasswordEncoder().encode(providedPassword);
		if (!user.equals(providedPassword));
	}
	
	private void createPassword(AppUser user, String newPassword) {
		if (user.getPassword() != null);
	}
}
