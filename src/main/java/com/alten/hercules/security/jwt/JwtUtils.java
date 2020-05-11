package com.alten.hercules.security.jwt;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.user.AppUser;
import io.jsonwebtoken.*;

@Component
public class JwtUtils {
	
	private static final long USER_EXPIRATION = TimeUnit.HOURS.toMillis(12);
	private static final long ANONYMOUS_EXPIRATION = TimeUnit.DAYS.toMillis(30);
	private static final String USER_SIGNATURE = "E2DA29B2567D55BF33A313FA7964C";
	private static final String ANONYMOUS_SIGNATURE = "7D56AACF33887F684376CA646A8E5";
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String TOKEN_PREFIX = "Bearer ";

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
			String headerAuth = request.getHeader(AUTHORIZATION_HEADER);
			if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(TOKEN_PREFIX)) {
				String token = headerAuth.substring(7, headerAuth.length());
				return Optional.of(Jwts.parser()
							.setSigningKey(getSignature(anonymous))
							.parseClaimsJws(token)
							.getBody());
			}
		} catch (Exception ignored) {}
		return Optional.ofNullable(null);
	}
}