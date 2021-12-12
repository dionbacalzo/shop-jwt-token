package com.shop.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.shop.constant.AppConstant;

public class HttpRequestUtil {

	private static final Logger logger = LogManager.getLogger(HttpRequestUtil.class);
	
	public static HttpServletRequest getCurrentHttpRequest(){
		logger.debug(AppConstant.METHOD_IN);
		HttpServletRequest toReturn = null;
	    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
	    if (requestAttributes instanceof ServletRequestAttributes) {
	        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
	        toReturn = request;
	    }
	    logger.debug(AppConstant.METHOD_OUT);
	    return toReturn;
	}
	
	public final String userIpAddress = getCurrentHttpRequest().getRemoteAddr();
	
}
