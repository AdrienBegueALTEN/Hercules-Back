package com.alten.hercules.security.jwt;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.user.AppUser;
import io.jsonwebtoken.*;

@Component
public class JwtUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
	private static final long USER_EXPIRATION = TimeUnit.HOURS.toMillis(12);
	private static final long ANONYMOUS_EXPIRATION = TimeUnit.DAYS.toMillis(30);
	private static final String USER_SIGNATURE = "E2DA29B2567D55BF33A313FA7964C";
	private static final String ANONYMOUS_SIGNATURE = "7D56AACF33887F684376CA646A8E5";

	public static String generateJwt(AppUser user) {
		return Jwts.builder()
				.setSubject(Long.toString(user.getId()))
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + USER_EXPIRATION))
				.signWith(SignatureAlgorithm.HS512, USER_SIGNATURE)
				.compact();
	}
	
	public static String generateJwt(Mission mission) {
		return Jwts.builder()
				.setSubject(Long.toString(mission.getId()))
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + ANONYMOUS_EXPIRATION))
				.claim("secret", mission.getSecret())
				.signWith(SignatureAlgorithm.HS512, ANONYMOUS_SIGNATURE)
				.compact();
	}
	
	private static String getSignature(boolean anonymous) {
		return anonymous ? ANONYMOUS_SIGNATURE : USER_SIGNATURE;
	}

	public static Optional<Claims> parseJwt(HttpServletRequest request, boolean anonymous) {
		try {
			String headerAuth = request.getHeader("Authorization");
			if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
				String token = headerAuth.substring(7, headerAuth.length());
				return Optional.of(Jwts.parser()
							.setSigningKey(getSignature(anonymous))
							.parseClaimsJws(token)
							.getBody());
			}
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
		return Optional.ofNullable(null);
	}
}