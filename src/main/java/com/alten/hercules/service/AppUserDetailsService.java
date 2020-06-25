package com.alten.hercules.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.alten.hercules.dao.user.UserDAO;
import com.alten.hercules.model.user.AppUser;

/**
 * Class used to retrieve the details of an user.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class AppUserDetailsService implements UserDetailsService {
	
	/**
	 * DAO for users
	 */
	@Autowired UserDAO userDAO;
	
	/**
	 * Function that returns the details of an user by using his email/username.
	 */
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AppUser user = userDAO.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

		return user;
	}
	

}
