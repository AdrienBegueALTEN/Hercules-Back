package com.alten.hercules.security.jwt;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Class for the management of the authentication for the http requests
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
	
	/**
	 * Interface used for the logging
	 */
	private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);
	
	/**
	 * Function that begins the authentication and sets the error
	 * @param request Http request
	 * @param response result from the logging
	 * @param authException exception for the authentication
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		logger.error("Unauthorized error: {}", authException.getMessage());
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
	}

}
