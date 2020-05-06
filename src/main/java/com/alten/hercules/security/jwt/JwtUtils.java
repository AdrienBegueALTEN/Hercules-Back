package com.alten.hercules.security.jwt;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.user.AppUser;
import io.jsonwebtoken.*;

@Component
public class JwtUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
	private static final long USER_EXP = TimeUnit.DAYS.toMillis(1);
	private static final long MISSION_EXP = TimeUnit.DAYS.toMillis(30);
	private static final String SIGNATURE_KEY = "EAD8FA553516B3281E16D4F696A4C278C14E31F5A7E49";
	private static final String ANONYMOUS_KEY = "anonymous";

	public static String generateJwt(AppUser user) {
		return Jwts.builder()
				.setSubject(Long.toString(user.getId()))
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + USER_EXP))
				.claim(ANONYMOUS_KEY, false)
				.signWith(SignatureAlgorithm.HS512, SIGNATURE_KEY)
				.compact();
	}
	
	public static String generateJwt(Mission mission) {
		return Jwts.builder()
				.setSubject(Long.toString(mission.getId()))
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + MISSION_EXP))
				.claim(ANONYMOUS_KEY, true)
				.signWith(SignatureAlgorithm.HS512, SIGNATURE_KEY)
				.compact();
	}

	public static Long getSubjectFromJwt(String token) {
		return Long.parseLong(Jwts.parser().setSigningKey(SIGNATURE_KEY).parseClaimsJws(token).getBody().getSubject());
	}

	public static EJwtValidation validateJwt(String token) {
		try {
			Boolean anonymous = (Boolean)Jwts.parser().setSigningKey(SIGNATURE_KEY).parseClaimsJws(token).getBody().get(ANONYMOUS_KEY);
			return anonymous ? EJwtValidation.ANONYMOUS : EJwtValidation.USER;
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
		} catch (ClassCastException e) {
			logger.error("JWT claims malformed: {}", e.getMessage());
		}
		return EJwtValidation.INVALID;
	}
}