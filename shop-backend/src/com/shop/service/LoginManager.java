package com.shop.service;

import org.springframework.stereotype.Service;

import com.shop.dto.Result;
import com.shop.dto.User;

@Service
public interface LoginManager {

	public Result login(User user);

	public Result signup(User user);

	public String getAuthenticationToken();

}
