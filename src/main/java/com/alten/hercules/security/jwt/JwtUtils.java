package com.alten.hercules.security.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.alten.hercules.model.user.AppUser;

import io.jsonwebtoken.*;

@Component
public class JwtUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
	private static final String JWT_SECRET = "7FC2DFF16D23F8914AD318F7156438AB88371C18A2C8BBC7A767DF9B92";
	private static final long JWT_EXP_MS = 86400000;

	public static String generateJWT(Authentication authentication) {
		AppUser user = (AppUser) authentication.getPrincipal();
		
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("sub", user.getId());
		claims.put("iat", new Date());
		claims.put("exp", new Date((new Date()).getTime() + JWT_EXP_MS));
		claims.put("firstname", user.getFirstname());
		claims.put("lastname", user.getLastname());
		claims.put("role", user.getRole());

		return Jwts.builder()
				.setClaims(claims)
				.signWith(SignatureAlgorithm.HS512, JWT_SECRET)
				.compact();
	}

	public static Long getIdFromJwtToken(String token) {
		return Long.parseLong(Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token).getBody().getSubject());
	}

	public static boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}
}