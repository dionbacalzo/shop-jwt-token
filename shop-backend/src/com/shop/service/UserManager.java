package com.shop.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.shop.dto.Result;
import com.shop.dto.User;

@Service
public interface UserManager {

	public List<User> getAccountsToReset();

	public List<User> resetAccounts(List<User> userList);
	
	public User retrieveByUsername(String username);

	public Result updatePassword(UserDetails userInSession, Object object);

	public User updateByUsername(String username, String userJSON, MultipartFile picture);
	
}
