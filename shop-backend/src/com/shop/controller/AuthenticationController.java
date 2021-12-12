package com.shop.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.constant.AppConstant;
import com.shop.dto.Result;
import com.shop.dto.User;
import com.shop.dto.adapter.UserAdapter;
import com.shop.service.LoginManager;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true") //allow CORS for angular
@RestController
public class AuthenticationController {
	
	private final Logger logger = LogManager.getLogger(getClass());
	
	@Autowired @Qualifier("loginManagerImpl")	
	private LoginManager loginManagerImpl;
	
	@RequestMapping(value = "login")
	protected ModelAndView viewLoginPage() throws Exception {
		logger.debug(AppConstant.METHOD_IN);
		
		ModelAndView model = new ModelAndView("login");
		
		//redirect if already logged in
		if(SecurityContextHolder.getContext().getAuthentication() != null) {
			for(GrantedAuthority auth : SecurityContextHolder.getContext().getAuthentication().getAuthorities()){
				if(!auth.getAuthority().trim().equals("ROLE_ANONYMOUS")){
					model = new ModelAndView("content");
					break;
				}
			}
		}
		
		logger.debug(AppConstant.METHOD_OUT);
		return model;
	}
	
	@RequestMapping(produces = MediaType.TEXT_PLAIN_VALUE, value = "loginUser")
	protected String login(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug(AppConstant.METHOD_IN);
		Result result = new Result();
		String json = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			User user = new UserAdapter(request);
			result = loginManagerImpl.login(user);
			json = mapper.writeValueAsString(result);
		} catch (Exception e){
			result = new Result(AppConstant.SHOP_LOGIN_UNSUCCESSFUL_STATUS, AppConstant.SHOP_LOGIN_UNSUCCESSFUL_MESSAGE_GENERIC);
			json = mapper.writeValueAsString(result);
			logger.error(e.getMessage());
		}
		
		logger.debug(AppConstant.METHOD_OUT);
		return json;
	}
	
	@RequestMapping(value = "signupUser")
	protected String signup( @RequestBody User user) throws Exception {
		logger.debug(AppConstant.METHOD_IN);
		
		Result result = new Result();
		String json = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			result = loginManagerImpl.signup(user);
			json = mapper.writeValueAsString(result);
		} catch (Exception e){
			result = new Result(AppConstant.SHOP_SIGNUP_UNSUCCESSFUL_STATUS, AppConstant.SHOP_SIGNUP_UNSUCCESSFUL_MESSAGE_GENERIC);
			json = mapper.writeValueAsString(result);
			logger.error(e.getMessage());
		}
		
		logger.debug(AppConstant.METHOD_OUT);
		
		return json;
	}
	
	/**
	 * retrieves the token for the currently logged in user
	 * @param authentication
	 * @return
	 */
	@RequestMapping(value = "retrieveToken")
    @ResponseBody
    public String getAuthenticationToken() {
		logger.debug(AppConstant.METHOD_IN);
		String token = "";
		try {
			token = loginManagerImpl.getAuthenticationToken();
		} catch (Exception e){
			logger.error(e.getMessage(), e);
		}
		logger.debug(AppConstant.METHOD_OUT);
        return token;
	}

	
}
