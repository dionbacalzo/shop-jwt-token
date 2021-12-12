package com.shop.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.shop.constant.AppConstant;

@Component
public class ShopAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	protected final Logger log = LogManager.getLogger(getClass());
	private RequestCache requestCache = new HttpSessionRequestCache();

	@Override
	public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws ServletException, IOException {
		log.debug(AppConstant.METHOD_IN);
		final SavedRequest savedRequest = requestCache.getRequest(request, response);
	
	    if (savedRequest == null) {
	        clearAuthenticationAttributes(request);
	        return;
	    }
	    final String targetUrlParameter = getTargetUrlParameter();
	    if (isAlwaysUseDefaultTargetUrl() || (targetUrlParameter != null && StringUtils.hasText(request.getParameter(targetUrlParameter)))) {
	        requestCache.removeRequest(request, response);
	        clearAuthenticationAttributes(request);
	    } else {
	    	clearAuthenticationAttributes(request);
	    }
	    log.debug(AppConstant.METHOD_OUT);
	}
	
	public void setRequestCache(final RequestCache requestCache) {
	    this.requestCache = requestCache;
	}

}
