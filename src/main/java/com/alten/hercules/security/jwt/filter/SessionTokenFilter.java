package com.alten.hercules.security.jwt.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alten.hercules.dao.user.UserDAO;
import com.alten.hercules.security.jwt.ETokenType;
import com.alten.hercules.security.jwt.JwtUtils;

import io.jsonwebtoken.Claims;

/**
 * Class that manages the token for a connected user
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class SessionTokenFilter extends OncePerRequestFilter  {
	
	/**
	 * DAO for the user
	 */
	@Autowired private UserDAO userDao;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
			String token = JwtUtils.parseToken(request).orElseThrow();
			Claims claims = JwtUtils.getClaims(token, ETokenType.SESSION).orElseThrow();
			Long userId = Long.parseLong(claims.getSubject());
			UserDetails userDetails = userDao.findById(userId).get();
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (Exception ignored) {}
		filterChain.doFilter(request, response);
	}
	
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().contains("anonymous");
    }

}