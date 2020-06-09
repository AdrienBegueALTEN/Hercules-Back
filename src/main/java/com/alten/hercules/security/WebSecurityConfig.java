package com.alten.hercules.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.alten.hercules.security.jwt.AuthEntryPointJwt;
import com.alten.hercules.security.jwt.filter.AnonymousTokenFilter;
import com.alten.hercules.security.jwt.filter.SessionTokenFilter;
import com.alten.hercules.service.AppUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired AppUserDetailsService service;
	
	@Bean
	public AppUserDetailsService appUserDetailsService() {
		return new AppUserDetailsService();
	}

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	@Bean
	public AnonymousTokenFilter anonymousTokenFilter() {
		return new AnonymousTokenFilter();
	}
	
	@Bean
	public SessionTokenFilter userTokenFilter() {
		return new SessionTokenFilter();
	}

	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(service).passwordEncoder(new BCryptPasswordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().cors().and()
			.authorizeRequests()
				.antMatchers("/hercules/auth/signin", 
						"/hercules/customers/logo/**",
						"/hercules/missions/projects/picture/**",
						"/hercules/missions/projects/skills-all",
						
						"/v2/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**").permitAll()
				.antMatchers("/hercules/missions/anonymous").anonymous()
				.antMatchers("/hercules/missions/new-project-anonymous/**").anonymous()
				.antMatchers("/hercules/missions/projects/anonymous/**").anonymous()
				.antMatchers("/hercules/auth/change-password-anonymous").anonymous()
				.anyRequest().authenticated()
			.and().exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
			.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.addFilterBefore(anonymousTokenFilter(), AnonymousAuthenticationFilter.class);
		http.addFilterBefore(userTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	}
}
