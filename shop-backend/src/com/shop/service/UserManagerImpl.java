package com.shop.service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambdaworks.crypto.SCryptUtil;
import com.shop.constant.AppConstant;
import com.shop.dao.UserDao;
import com.shop.domain.UserDomainObject;
import com.shop.dto.Result;
import com.shop.dto.User;
import com.shop.dto.adapter.UserAdapter;

@Service
@Component("userManagerImpl")
public class UserManagerImpl implements UserManager {

	protected final Logger logger = LogManager.getLogger(getClass());

	@Autowired
	private UserDao userDaoImpl;
	
	@Autowired
    private UserDetailsService userDetailsService;

	/**
	 * Retrieves a list of accounts that exceeded the maximum login attempt
	 */
	@Override
	public List<User> getAccountsToReset() {
		logger.debug(AppConstant.METHOD_IN);

		List<User> userList = new ArrayList<User>();
		List<UserDomainObject> userDomainList = userDaoImpl.findByTryCounter(3);

		for (UserDomainObject userdomain : userDomainList) {
			userList.add(new UserAdapter(userdomain));
		}

		logger.debug(AppConstant.METHOD_OUT);

		return userList;
	}

	/**
	 * Resets the try counter of a list of users
	 * @return List of users that still exceeded the maximum login attempt 
	 */
	@Override
	public List<User> resetAccounts(List<User> userList) {
		logger.debug(AppConstant.METHOD_IN);
		List<UserDomainObject> userdomainList = new ArrayList<UserDomainObject>();
		for (User user : userList) {
			UserDomainObject userdomain = userDaoImpl.findByUserName(user.getUsername());
			userdomain.setTryCounter(0);
			userdomainList.add(userdomain);
		}
		if (!userdomainList.isEmpty()) {
			userDaoImpl.saveAll(userdomainList);
		}
		logger.debug(AppConstant.METHOD_OUT);
		return getAccountsToReset();
	}

	/**
	 * Retrieves a User object that matches a given username
	 * @param username the unique identifier of an account
	 * @return User object containing all the details
	 */
	@Override
	public User retrieveByUsername(String username) {
		logger.debug(AppConstant.METHOD_IN);

		UserDomainObject user = userDaoImpl.findByUserName(username);

		logger.debug(AppConstant.METHOD_OUT);
		return new UserAdapter(user);
	}

	/**
	 * Checks if the content of an account is valid
	 * @param user
	 * @param picture the profile picture of the account
	 * @return boolean
	 */
	private boolean validateInput(User user, MultipartFile picture) {
		boolean valid = true;
		if (user == null) {
			valid = false;
		} else if (user.getFirstname() == null || user.getFirstname().trim().isEmpty()) {
			valid = false;
		} else if (user.getLastname() == null || user.getLastname().trim().isEmpty()) {
			valid = false;
		} else if(picture != null) {
			if(!AppConstant.contentTypes.contains(picture.getContentType())) {
				valid = false;
				logger.error(AppConstant.SHOP_PROFILE_INVALID_PICTURE);
			}
		}

		return valid;
	}

	/**
	 * Updates the information of a user that matches a given username
	 */
	@Override
	public User updateByUsername(String username, String userJSON, MultipartFile picture) {
		logger.debug(AppConstant.METHOD_IN);
		
		User user = null;
		try {
			user = new ObjectMapper().readValue(userJSON, User.class);
		} catch (JsonParseException e) {
			logger.error(AppConstant.SHOP_PROFILE_INVALID_INPUT);
		} catch (JsonMappingException e) {
			logger.error(AppConstant.SHOP_PROFILE_INVALID_INPUT);
		} catch (IOException e) {
			logger.error(AppConstant.SHOP_PROFILE_INVALID_INPUT);
		}

		User updatedUser = null;
		if (validateInput(user, picture)) {
			if(picture != null) {
				try {
					user.setPicture(picture.getBytes());
				} catch (IOException e) {
					logger.error(AppConstant.SHOP_PROFILE_INVALID_PICTURE);
				}
			}
			UserDomainObject updatedUserDomainObj = userDaoImpl.updateByUserName(username, user);			
			if (updatedUserDomainObj != null) {
				updatedUser = new UserAdapter(updatedUserDomainObj);
				logger.debug(MessageFormat.format(AppConstant.SHOP_PROFILE_UPDATE_LOG_SUCCESS,username));
			} else {
				logger.debug(MessageFormat.format(AppConstant.SHOP_PROFILE_UPDATE_LOG_FAIL,username));
			}
		} else {
			logger.debug(AppConstant.SHOP_PROFILE_INVALID_INPUT);
		}

		logger.debug(AppConstant.METHOD_OUT);
		return updatedUser;
	}

	/**
	 * Check if the content of the password fields are valid
	 * @param oldPassword the current password the user wish to change
	 * @param newPassword the new password
	 * @param newPasswordRetype the old password retyped to compare
	 * @return Result containing validation status and message 
	 */
	private Result validatePasswords(String oldPassword, String newPassword, String newPasswordRetype) {
		Result result = new Result(AppConstant.SHOP_PASSWORD_UPDATE_SUCCESSFUL_STATUS,
				AppConstant.SHOP_PASSWORD_UPDATE_MESSAGE_SUCCESS);
		if (oldPassword == null || oldPassword.trim().isEmpty()) {
			result = new Result(AppConstant.SHOP_PASSWORD_UPDATE_UNSUCCESSFUL_STATUS,
					AppConstant.SHOP_PASSWORD_UPDATE_UNSUCCESSFUL_MISSING_PASSWORD);
		} else if (newPassword == null || newPassword.trim().isEmpty()) {
			result = new Result(AppConstant.SHOP_PASSWORD_UPDATE_UNSUCCESSFUL_STATUS,
					AppConstant.SHOP_PASSWORD_UPDATE_UNSUCCESSFUL_MISSING_NEWPASSWORD);
		} else if (newPasswordRetype == null || newPasswordRetype.trim().isEmpty()) {
			result = new Result(AppConstant.SHOP_PASSWORD_UPDATE_UNSUCCESSFUL_STATUS,
					AppConstant.SHOP_PASSWORD_UPDATE_UNSUCCESSFUL_MISSING_NEWPASSWORD2);
		} else if (!newPasswordRetype.equals(newPassword)) {
			result = new Result(AppConstant.SHOP_PASSWORD_UPDATE_UNSUCCESSFUL_STATUS,
					AppConstant.SHOP_PASSWORD_UPDATE_UNSUCCESSFUL_INCORRECT_NEWPASSWORD);
		} else if (oldPassword.equals(newPassword)) {
			result = new Result(AppConstant.SHOP_PASSWORD_UPDATE_UNSUCCESSFUL_STATUS,
					AppConstant.SHOP_PASSWORD_UPDATE_UNSUCCESSFUL_MATCHING_NEWPASSWORD);
		}
		
		return result;
	}

	/**
	 * Updates the password of the currently logged in user
	 */
	@Override
	public Result updatePassword(UserDetails userInSession, Object object) {
		logger.debug(AppConstant.METHOD_IN);

		Result result = new Result();
		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		try {
			json = mapper.writeValueAsString(object);

			JsonNode jsonNode = mapper.readTree(json);
			String oldPassword = jsonNode.get("password").asText();
			String newPassword = jsonNode.get("newPassword").asText();
			String newPasswordRetype = jsonNode.get("newPasswordRetype").asText();
			result = validatePasswords(oldPassword, newPassword, newPasswordRetype);
			if (result.getStatus() != AppConstant.SHOP_PASSWORD_UPDATE_UNSUCCESSFUL_STATUS) {
				// check if password given matches current password
				boolean matched = SCryptUtil.check(oldPassword, userInSession.getPassword());
				if (matched) {
					result = userDaoImpl.updatePasswordByUserName(userInSession.getUsername(),
							SCryptUtil.scrypt(newPassword, 16, 16, 16));
					// update the current password in session
					if (result.getStatus() != AppConstant.SHOP_PASSWORD_UPDATE_UNSUCCESSFUL_STATUS) {
						UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
								userDetailsService.loadUserByUsername(userInSession.getUsername()), newPassword, userInSession.getAuthorities());
						SecurityContextHolder.getContext().setAuthentication(authToken);
						logger.debug(userInSession.getUsername() + " " + result.getMessage());
					}
				} else {
					result = new Result(AppConstant.SHOP_PASSWORD_UPDATE_UNSUCCESSFUL_STATUS,
							AppConstant.SHOP_PASSWORD_UPDATE_UNSUCCESSFUL_INCORRECT_PASSWORD);
					logger.debug(MessageFormat.format(AppConstant.SHOP_PASSWORD_UPDATE_LOG_MESSAGE_FAIL,userInSession.getUsername(), result.getMessage()));
				}
			} else {
				logger.debug(MessageFormat.format(AppConstant.SHOP_PASSWORD_UPDATE_LOG_MESSAGE_FAIL,userInSession.getUsername(), result.getMessage()));
			}

		} catch (IOException e) {
			result = new Result(AppConstant.SHOP_LOGIN_UNSUCCESSFUL_STATUS,
					AppConstant.SHOP_LOGIN_UNSUCCESSFUL_MESSAGE_GENERIC);
			logger.error(e.getMessage());
		} catch (Exception e) {
			result = new Result(AppConstant.SHOP_LOGIN_UNSUCCESSFUL_STATUS,
					AppConstant.SHOP_LOGIN_UNSUCCESSFUL_MESSAGE_GENERIC);
			logger.error(e.getMessage());
		}
		logger.debug(AppConstant.METHOD_OUT);

		return result;
	}

}
