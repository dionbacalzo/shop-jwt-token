package com.shop.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

public class ShopLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		if (request.getHeader("origin") != null && request.getHeader("host") != null
				&& request.getHeader("origin").contains(request.getHeader("host"))) {
			super.onLogoutSuccess(request, response, authentication);
		} else {
			// for logout where the same host has a different port (for the angular app)
			response.addHeader("Access-Control-Allow-Origin", "http://localhost:4200");
			response.addHeader("Access-Control-Allow-Credentials", "true");
			new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK);
		}
	}

}
