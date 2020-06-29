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

/**
 * Class that contains method to manage the json web tokens for the authentication
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
@Component
public class JwtUtils {
	
	/**
	 * Duration of a mission's modification
	 */
	private static final long MISSION_EXPIRATION = TimeUnit.DAYS.toMillis(30);
	/**
	 * Duration of a link for a password's creation
	 */
	private static final long PASSWORD_CREATION_EXPIRATION = TimeUnit.DAYS.toMillis(7);
	/**
	 * Duration of a session for a connected user
	 */
	private static final long SESSION_EXPIRATION = TimeUnit.HOURS.toMillis(12);
	/**
	 * Signature for an anonymous request for mission's modification
	 */
	private static final String MISSION_SIGNATURE = "7D56AACF33887F684376CA646A8E5";
	/**
	 * Signature for an anonymous request for password's creation
	 */
	private static final String PASSWORD_CREATION_SIGNATURE = "4D6EB7557B6DE6FEC5E777F14181E";
	/**
	 * Signature for a request from a connected user in a session
	 */
	private static final String SESSION_SIGNATURE = "E2DA29B2567D55BF33A313FA7964C";
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String TOKEN_PREFIX = "Bearer ";
	
	/**
	 * Function that builds an anonymous token for a mission's modification
	 * @param mission Mission that can be modified
	 * @return String containing the token
	 */
	public static String generateMissionToken(Mission mission) {
		return buildAnonymousToken(mission.getId(), MISSION_EXPIRATION, mission.getSecret(), MISSION_SIGNATURE);
	}
	
	/**
	 * Function that builds an anonymous token for a password's creation
	 * @param user User that creates his password
	 * @return String containing the token
	 */
	public static String generatePasswordCreationToken(AppUser user) {
		return buildAnonymousToken(user.getId(), PASSWORD_CREATION_EXPIRATION, user.getSecret(), PASSWORD_CREATION_SIGNATURE);
	}
	
	/**
	 * Function that builds a Json web token with given details
	 * @param subject identifier 
	 * @param expiration duration before the expiration
	 * @param secret secret 
	 * @param signature Signature of the corresponding type of request
	 * @return String containing the token
	 */
	private static String buildAnonymousToken(Long subject, Long expiration, int secret, String signature) {
		return Jwts.builder()
				.setSubject(Long.toString(subject))
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + expiration))
				.claim("secret", secret)
				.signWith(SignatureAlgorithm.HS512, signature)
				.compact();
	}
	
	/**
	 * Function that builds a Json web token with given details
	 * @param user User connected in the session
	 * @return String containing the token
	 */
	public static String generateSessionToken(AppUser user) {
		return Jwts.builder()
				.setSubject(Long.toString(user.getId()))
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + SESSION_EXPIRATION))
				.signWith(SignatureAlgorithm.HS512, SESSION_SIGNATURE)
				.compact();
	}
	
	/**
	 * Function that checks the type of a token : session or anonymous mission's modification or anonymous password's creation
	 * @param token Token to be checked
	 * @return Type of the token 
	 */
	public static Optional<ETokenType> getTokenType(String token) {
		ETokenType tokenType = null;
		try {
			Jwts.parser().setSigningKey(SESSION_SIGNATURE).parseClaimsJws(token);
			tokenType = ETokenType.SESSION;
		} catch (Exception notSessionType) {
			try {
				Jwts.parser().setSigningKey(MISSION_SIGNATURE).parseClaimsJws(token);
				tokenType = ETokenType.MISSION;
			} catch (Exception notMissionType) {
				try {
					Jwts.parser().setSigningKey(PASSWORD_CREATION_SIGNATURE).parseClaimsJws(token);
					tokenType = ETokenType.PASSWORD_CREATION;
				} catch (Exception notPasswordCreationType) {}
			}
		}
		return Optional.ofNullable(tokenType);
	}
	
	/**
	 * Function that retrieves the token from the header of an http request
	 * @param request Http request 
	 * @return String with the token
	 */
	public static Optional<String> parseToken(HttpServletRequest request) {
		String token = null;
		String headerAuth = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(TOKEN_PREFIX))
			token = headerAuth.substring(7, headerAuth.length());
		return Optional.ofNullable(token);
	}
	
	/**
	 * Function that generate a set of Claim with the given token and its type
	 * @param token Token
	 * @param tokenType Type of the token
	 * @return 
	 */
	public static Optional<Claims> getClaims(String token, ETokenType tokenType) {
		Claims claims = null;
		try {
			String signature = null;
			switch (tokenType) {
			case MISSION:
				signature = MISSION_SIGNATURE;
				break;
			case PASSWORD_CREATION:
				signature = PASSWORD_CREATION_SIGNATURE;
				break;
			case SESSION:
				signature = SESSION_SIGNATURE;
			}
			claims = Jwts.parser().setSigningKey(signature).parseClaimsJws(token).getBody();
		} catch (Exception ignored) {}
		return Optional.ofNullable(claims);
	}
}