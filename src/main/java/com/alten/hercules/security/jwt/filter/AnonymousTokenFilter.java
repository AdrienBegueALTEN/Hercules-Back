package com.alten.hercules.security.jwt.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alten.hercules.dao.mission.MissionDAO;
import com.alten.hercules.dao.user.UserDAO;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.user.EAuthorities;
import com.alten.hercules.security.jwt.ETokenType;
import com.alten.hercules.security.jwt.JwtUtils;

import io.jsonwebtoken.Claims;

public class AnonymousTokenFilter extends OncePerRequestFilter {

	@Autowired private MissionDAO missionDao;
	@Autowired private UserDAO userDao;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
			String token = JwtUtils.parseToken(request).orElseThrow();
			ETokenType tokenType = JwtUtils.getTokenType(token).orElseThrow();
			Claims claims = JwtUtils.getClaims(token, tokenType).orElseThrow();
			switch (tokenType) {
				case MISSION:
					Long missionId = Long.parseLong(claims.getSubject());
					Mission mission = missionDao.findById(missionId).get();
					Integer tokenSecret = (Integer)claims.get("secret");
					if (tokenSecret == mission.getSecret()) {
						Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
						authorities.add(new SimpleGrantedAuthority(EAuthorities.MISSION.name()));
						AnonymousAuthenticationToken authentication =  new AnonymousAuthenticationToken("mission", mission, authorities);
						authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						SecurityContextHolder.getContext().setAuthentication(authentication);
					}
					break;
				case PASSWORD_CREATION:
					Long userId = Long.parseLong(claims.getSubject());
					UserDetails userDetails = userDao.findById(userId).get();
					Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
					authorities.add(new SimpleGrantedAuthority(EAuthorities.CHANGE_PASSWORD.name()));
					AnonymousAuthenticationToken authentication = new AnonymousAuthenticationToken("user", userDetails, authorities);
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);
				default:
			}
		} catch (Exception ignored) {}
		filterChain.doFilter(request, response);
	}
	
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().contains("anonymous");
    }
}