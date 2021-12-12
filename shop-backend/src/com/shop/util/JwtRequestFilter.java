package com.shop.util;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.shop.constant.AppConstant;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
	
	private final Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	//@Autowired
	//private JwtService jwtService;
	

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		logger.debug(AppConstant.METHOD_IN);
		final String requestTokenHeader = request.getHeader("Authorization");
		String username = null;
		String jwtToken = null;
		// JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			try {
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
			} catch (IllegalArgumentException e) {
				logger.debug("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				logger.debug("JWT Token has expired");
			} catch (SignatureException e) {
				logger.debug("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.");
			}
		} else {
			logger.warn("JWT Token does not begin with Bearer String");
		}
		// Once we get the token validate it.
		if (username != null && (SecurityContextHolder.getContext().getAuthentication() == null ||
				SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
				
				
				//UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
				String status = AppConstant.SUCCESSFUL_STATUS; 
						//jwtService.findUser(username);
				UserDetails userDetails = new User(username, "", new ArrayList<>());
				
				// if token is valid configure Spring Security to manually set authentication
				if (status.equalsIgnoreCase(AppConstant.SUCCESSFUL_STATUS) && 
						jwtTokenUtil.validateToken(jwtToken, username)) {
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
							new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					usernamePasswordAuthenticationToken
							.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					// After setting the Authentication in the context, we specify
					// that the current user is authenticated. So it passes the
					// Spring Security Configurations successfully.
					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				}
			}
		chain.doFilter(request, response);
		
		logger.debug(AppConstant.METHOD_OUT);
	}
	
}