package com.shop.dto.adapter;

import javax.servlet.http.HttpServletRequest;

import com.lambdaworks.crypto.SCryptUtil;
import com.shop.domain.UserDomainObject;
import com.shop.dto.User;

public class UserAdapter extends User {

	public UserAdapter(UserDomainObject user) {
		setUsername(user.getUserName());
		setFirstname(user.getFirstname());
		setLastname(user.getLastname());
		setRole(user.getRole());
		if (user.getPicture() != null) {
			setPicture(user.getPicture().getData());
		}
	}
	
	public UserAdapter(User user) {
		setUsername(user.getUsername());
		setPassword(SCryptUtil.scrypt(user.getPassword(), 16, 16, 16));
	}
	
	public UserAdapter(HttpServletRequest request) {
		setUsername(request.getParameter("username"));
		setPassword(request.getParameter("password"));
	}
	
}
