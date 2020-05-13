package com.alten.hercules.security.jwt.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import com.alten.hercules.dao.mission.MissionDAO;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.user.EAuthorities;
import com.alten.hercules.security.jwt.JwtUtils;

import io.jsonwebtoken.Claims;

public class AnonymousTokenFilter extends HttpFilter {

	@Autowired private MissionDAO missionDao;

	@Override
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
			Claims claims = JwtUtils.parseJwt(request, true).orElseThrow();
			Long missionId = Long.parseLong(claims.getSubject());
			Mission mission = missionDao.findById(missionId).get();
			Integer tokenSecret = (Integer)claims.get("secret");
			if (tokenSecret == mission.getSecret()) {
				Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
				authorities.add(new SimpleGrantedAuthority(EAuthorities.ANONYMOUS.name()));
				AnonymousAuthenticationToken authentication =  new AnonymousAuthenticationToken("anonymous", mission.getId(), authorities);
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception ignored) {}
		filterChain.doFilter(request, response);
	}
}