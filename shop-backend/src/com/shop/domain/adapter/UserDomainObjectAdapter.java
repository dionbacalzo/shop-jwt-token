package com.shop.domain.adapter;

import com.lambdaworks.crypto.SCryptUtil;
import com.shop.domain.UserDomainObject;
import com.shop.dto.User;

public class UserDomainObjectAdapter extends UserDomainObject {

	public static UserDomainObject parseUser(User user){
		UserDomainObject userDomainObject = new UserDomainObject();
		userDomainObject.setUserName(user.getUsername());
		userDomainObject.setFirstname(user.getFirstname());
		userDomainObject.setLastname(user.getLastname());
		userDomainObject.setPassword(SCryptUtil.scrypt(user.getPassword(), 16, 16, 16));
		userDomainObject.setRole(user.getRole());
		userDomainObject.setTryCounter(0);
		
		return userDomainObject;
	}
	
}
