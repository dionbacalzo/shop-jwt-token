package com.shop.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.shop.constant.AppConstant;

/**
 * get property value from a file 
 * @author 81015414
 *
 */
public class PropertyUtil {
	
	protected static final Logger logger = LogManager.getLogger(PropertyUtil.class);
	
	/**
	 * try to get property using default property file
	 * @param propertyKey
	 * @return
	 */
	public static String getProperty(String propertyKey) {
		return getProperty(propertyKey, AppConstant.PROPERTY_FILE);
	}

	/**
	 * method to retrieve property from a file
	 * @param propertyKey
	 * @param filename
	 * @return
	 */
	public static String getProperty(String propertyKey, String filename) {
		logger.debug(AppConstant.METHOD_IN);
		String value = "";

		Properties prop = new Properties();
		InputStream input = null;
		try {
			if(new File(filename).isAbsolute()){
				input = new FileInputStream(filename);
			} else {
				ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				input = classLoader.getResourceAsStream(filename);
			}
			logger.info("Using " + filename + " as property file for " + propertyKey);
			prop.load(input);

			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				if(key.equals(propertyKey)){
					value = prop.getProperty(key);
					break;
				}
			}
			if (value == null) {
				value = "";
			}

		} catch (Exception ex) {
			logger.error("Read property file error: " + ex.getMessage());
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.error("Read property file error: " + e.getMessage());
				}
			}
		}		
		logger.info("Property: " + propertyKey + " Value : " + value);
		
		logger.debug(AppConstant.METHOD_OUT);
		return value;
	}

}
