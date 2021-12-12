package com.shop.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.shop.constant.AppConstant;
import com.shop.util.HttpRequestUtil;
import com.shop.util.JwtTokenUtil;


@Service
@Primary
public class BaseService {
	
	private static final Logger logger = LogManager.getLogger(BaseService.class);

	@Autowired
	protected JwtTokenUtil jwtTokenUtil;
	
	/**
	 * Used to retrieve the user id of the current transaction
	 * @return
	 */
	public String getCurrentUserId() {
		logger.debug(AppConstant.METHOD_IN);
		String username = null;
		try {
			final String requestTokenHeader = HttpRequestUtil.getCurrentHttpRequest().getHeader("Authorization");
			if (requestTokenHeader != null && !requestTokenHeader.isEmpty() && requestTokenHeader.startsWith("Bearer ")) {
				String jwtToken = requestTokenHeader.substring(7);
				if (jwtToken != null) {
					logger.debug(jwtTokenUtil);
					username = jwtTokenUtil.getUsernameFromToken(jwtToken.trim());
				}
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.debug(AppConstant.METHOD_OUT);
		return username;
	}

}
