package com.shop.security;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.shop.dao.UserDao;
import com.shop.domain.UserDomainObject;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

	private final Logger logger = LogManager.getLogger(getClass());

	@Autowired
	private UserDao userDao;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.debug("Authenticating " + username);

		UserDomainObject user = userDao.findByUserName(username);
		if (user == null) {
			throw new UsernameNotFoundException("User " + username + " was not found in the database");
		} 

		Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getRole());
		grantedAuthorities.add(grantedAuthority);

		return new User(username, user.getPassword(), grantedAuthorities);
	}

}
