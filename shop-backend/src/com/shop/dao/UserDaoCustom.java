package com.shop.dao;

import com.shop.domain.UserDomainObject;
import com.shop.dto.Result;
import com.shop.dto.User;

public interface UserDaoCustom {
	
	public UserDomainObject updateByUserName(String username, User user);
	
	public Result updatePasswordByUserName(String username, String newPassword);
	
}
