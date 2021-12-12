package com.shop.service;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.lambdaworks.crypto.SCryptUtil;
import com.shop.constant.AppConstant;
import com.shop.dao.TokenDao;
import com.shop.dao.UserDao;
import com.shop.domain.UserDomainObject;
import com.shop.domain.adapter.UserDomainObjectAdapter;
import com.shop.dto.Result;
import com.shop.dto.User;
import com.shop.dto.adapter.UserAdapter;
import com.shop.exception.ShopException;
import com.shop.util.JwtTokenUtil;

@Service
@Component("loginManagerImpl")
public class LoginManagerImpl extends BaseService implements LoginManager, AuthenticationProvider  {

	protected final Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	private UserDao userDaoImpl;
	
	@Autowired
	private TokenDao tokenDao;
	
	@Autowired
    private UserDetailsService userDetailsService;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	private static String[] ROLE = {
			"USER", 
			"ADMIN", 
	};
	
	/**
	 * validate user content
	 * @param user
	 * @return
	 */
	private boolean validateInput(User user){
		boolean valid = true;
		if(user.getUsername() == null || user.getUsername().trim().isEmpty()){
			valid = false;
		}
		if(user.getPassword() == null || user.getPassword().trim().isEmpty()){
			valid = false;
		}
		
		return valid;
	}
	
	/**
	 * validate user content when signing up
	 * @param user
	 * @return
	 */
	private boolean validateSignUpInput(User user){
		boolean valid = validateInput(user);
		if(user.getFirstname() == null || user.getFirstname().trim().isEmpty()){
			valid = false;
		}
		if(user.getLastname() == null || user.getLastname().trim().isEmpty()){
			valid = false;
		}
		if(user.getRole() == null || user.getRole().trim().isEmpty()){
			valid = false;
		} else {
			if(!Arrays.asList(ROLE).contains(user.getRole())){
				valid = false;
			}
		}
		return valid;
	}
	
	/**
	 * login a user
	 * @param user
	 * @return result: an object containing a status and message of the login result 
	 */
	@Override
	public Result login(User user) {
		logger.debug(AppConstant.METHOD_IN);
		Result result = new Result(AppConstant.SHOP_LOGIN_SUCCESSFUL_STATUS, AppConstant.SHOP_LOGIN_SUCCESSFUL_MESSAGE);
		try {
			if(validateInput(user)){
				result = findUser(user);
			} else {
				logger.debug(AppConstant.SHOP_LOGIN_UNSUCCESSFUL_STATUS + user.getUsername());
				result = new Result(AppConstant.SHOP_LOGIN_UNSUCCESSFUL_STATUS, AppConstant.SHOP_LOGIN_UNSUCCESSFUL_MESSAGE_GENERIC);
			}
		} catch(Exception e){
			logger.error(e.getMessage());
			result = new Result(AppConstant.SHOP_LOGIN_UNSUCCESSFUL_STATUS, AppConstant.SHOP_LOGIN_UNSUCCESSFUL_MESSAGE_GENERIC);
		}
		logger.debug(AppConstant.METHOD_OUT);
		return result;
	}
	
	public Result findUser(User user){
		logger.debug(AppConstant.METHOD_IN);
		Result result = new Result(AppConstant.SHOP_LOGIN_SUCCESSFUL_STATUS, AppConstant.SHOP_LOGIN_SUCCESSFUL_MESSAGE);
		try {
			UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

			Authentication auth = authenticate(authReq);
	        
	        //SecurityContextHolder.getContext().setAuthentication(auth);
	        UserDetails userInSession = (UserDetails) auth.getPrincipal();
	        User userloggedIn = new UserAdapter(userDaoImpl.findByUserName(userInSession.getUsername()));
	        
	        // set token
	        Long expirationTime = Long.valueOf(AppConstant.SESSION_TIMEOUT);
	        Long countdownTime = Long.valueOf(60); // default to 60 seconds 
	        if (expirationTime != null) {
				if (expirationTime  > countdownTime) {
					countdownTime = expirationTime - countdownTime;
				}
			}
	        Map<String, Object> claims = new HashMap<String, Object>();
	        claims.put("issuedDate", new Date(System.currentTimeMillis()));
	        claims.put("expiryDate", new Date(System.currentTimeMillis() + expirationTime  * 1000 ));
	        claims.put("countdownDate", new Date(System.currentTimeMillis() + countdownTime * 1000 ));
	        claims.put("role", userloggedIn.getRole());
	        final String token = jwtTokenUtil.doGenerateToken(claims,userloggedIn.getUsername());
        	if (token != null && !token.trim().isEmpty()) {
        		userloggedIn.setToken(token);
        	} else {
        		throw new Exception(AppConstant.SHOP_LOGIN_UNSUCCESSFUL_MESSAGE_TOKEN);
        	}
	        
	        result.setDetails(userloggedIn);
	        
		} catch(ShopException e){
			logger.debug(e.getMessage());
			result = new Result(AppConstant.SHOP_LOGIN_UNSUCCESSFUL_STATUS, e.getMessage());
		} catch(Exception e){
			logger.error(e.getMessage());
			result = new Result(AppConstant.SHOP_LOGIN_UNSUCCESSFUL_STATUS, AppConstant.SHOP_LOGIN_UNSUCCESSFUL_MESSAGE_GENERIC);
		}
		logger.debug(AppConstant.METHOD_OUT);
		return result;
	}
	
	/**
	 * Check if the login attempt is valid
	 * @param Authentication
	 * @return Authentication 
	 */
	@Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
		Result result = new Result(AppConstant.SHOP_LOGIN_SUCCESSFUL_STATUS, AppConstant.SHOP_LOGIN_SUCCESSFUL_MESSAGE);
		UsernamePasswordAuthenticationToken authToken = null;
		
		String username = auth.getName();
        String password = auth.getCredentials().toString();

        UserDomainObject availableUser = userDaoImpl.findByUserName(username);
		if(availableUser == null){
			result = new Result(AppConstant.SHOP_LOGIN_UNSUCCESSFUL_STATUS, AppConstant.SHOP_LOGIN_UNSUCCESSFUL_MESSAGE_GENERIC);
			logger.debug(MessageFormat.format(AppConstant.SHOP_USER_NOT_FOUND, username));
		} else {
			if(availableUser.getTryCounter() >= 3){
				logger.debug(MessageFormat.format(AppConstant.SHOP_USER_EXCEEDED_LOGIN_ATTEMPT, username));
				result = new Result(AppConstant.SHOP_LOGIN_UNSUCCESSFUL_STATUS, AppConstant.SHOP_LOGIN_UNSUCCESSFUL_MESSAGE_LIMIT);
			} else {
				boolean matched = SCryptUtil.check(password, availableUser.getPassword());
				if(!matched){
					//add to total login again
					if(availableUser.getTryCounter() < 4){
						availableUser.setTryCounter(availableUser.getTryCounter()+1);
					}
					userDaoImpl.save(availableUser);
					logger.debug(MessageFormat.format(AppConstant.SHOP_USER_PASSWORD_MISMATCH, username));
					result = new Result(AppConstant.SHOP_LOGIN_UNSUCCESSFUL_STATUS, AppConstant.SHOP_LOGIN_UNSUCCESSFUL_MESSAGE_GENERIC);
				}
			}
		}
		
		if(result.getStatus().equalsIgnoreCase(AppConstant.SHOP_LOGIN_SUCCESSFUL_STATUS)){
			//get role
			Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
            grantedAuthorities.add(new SimpleGrantedAuthority(availableUser.getRole()));
	        authToken = new UsernamePasswordAuthenticationToken(userDetailsService.loadUserByUsername(username), password, grantedAuthorities);
	        //reset counter for a successful login
	        if(availableUser.getTryCounter() > 0){
				availableUser.setTryCounter(0);
				userDaoImpl.save(availableUser);
			}
		} else {
			throw new ShopException(result.getMessage());
		}
		
        return authToken;
    }
 
    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
    
    /**
     * Attempt to sign up a given user
     * @param user
	 * @return result: an object containing a status and message when signing up
     */
    @Override
	public Result signup(User user) {
		logger.debug(AppConstant.METHOD_IN);
		
		Result result = new Result(AppConstant.SHOP_SIGNUP_SUCCESSFUL_STATUS, AppConstant.SHOP_SIGNUP_SUCCESSFUL_MESSAGE);
		UserDomainObject availableUser = null;
		if(validateSignUpInput(user)){
			try {
				availableUser = userDaoImpl.insert(UserDomainObjectAdapter.parseUser(user));
				
				if(availableUser == null){
					logger.error(AppConstant.SHOP_SIGNUP_STORE_UNSUCCESSFUL);
					result = new Result(AppConstant.SHOP_SIGNUP_UNSUCCESSFUL_STATUS, AppConstant.SHOP_SIGNUP_UNSUCCESSFUL_MESSAGE_GENERIC);
				} else {
					logger.debug(MessageFormat.format(AppConstant.SHOP_SIGNUP_STORE_SUCCESSFUL, availableUser.getUserName()));
				}
				
			} catch (DuplicateKeyException e) {
				logger.debug(AppConstant.SHOP_SIGNUP_STORE_UNSUCCESSFUL);
				result = new Result(AppConstant.SHOP_SIGNUP_UNSUCCESSFUL_STATUS, AppConstant.SHOP_SIGNUP_DUPLICATE_MESSAGE);
			} catch (Exception e) {
				logger.error(AppConstant.SHOP_SIGNUP_STORE_UNSUCCESSFUL);
				result = new Result(AppConstant.SHOP_SIGNUP_UNSUCCESSFUL_STATUS, AppConstant.SHOP_SIGNUP_UNSUCCESSFUL_MESSAGE_GENERIC);
			}
			
		} else {
			logger.error(AppConstant.SHOP_SIGNUP_INVALID_CREDENTIALS);
			result = new Result(AppConstant.SHOP_SIGNUP_UNSUCCESSFUL_STATUS, AppConstant.SHOP_SIGNUP_UNSUCCESSFUL_MESSAGE_GENERIC);
		}
		
		logger.debug(AppConstant.METHOD_OUT);
		
		return result;
	}
    
    @Override
    public String getAuthenticationToken() {
		logger.debug(AppConstant.METHOD_IN);
		String token = "";
		try {
			if (getCurrentUserId() != null && !getCurrentUserId().trim().isEmpty()) {
				String username = getCurrentUserId();
				logger.debug("Getting Token of User (" + username + ")");
				User user = new UserAdapter(userDaoImpl.findByUserName(username));
				if (user != null && user.getUsername() != null) {
					// set token
			        Long expirationTime = Long.valueOf(AppConstant.SESSION_TIMEOUT);
			        Long countdownTime = Long.valueOf(60); // default to 60 seconds 
			        if (expirationTime != null) {
						if (expirationTime  > countdownTime) {
							countdownTime = expirationTime - countdownTime;
						}
					}
			        Map<String, Object> claims = new HashMap<String, Object>();
			        claims.put("issuedDate", new Date(System.currentTimeMillis()));
			        claims.put("expiryDate", new Date(System.currentTimeMillis() + expirationTime  * 1000 ));
			        claims.put("countdownDate", new Date(System.currentTimeMillis() + countdownTime * 1000 ));
			        claims.put("role", user.getRole());
			        token = jwtTokenUtil.doGenerateToken(claims,String.valueOf(user.getUsername()));
			    	logger.debug("Token = " + token);
				} else {
					logger.error("No user is retrieved from username " + username);
				}
			} else {
				logger.error("No userid is retrieved from Authentication Header");
			}
		} catch (Exception e){
			logger.error(e.getMessage(), e);
		}
		logger.debug(AppConstant.METHOD_OUT);
        return token;
    }
    
}
