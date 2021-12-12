package com.shop.util;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.shop.constant.AppConstant;
import com.shop.exception.ShopException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class DateUtil {
	
	protected static final Logger logger = LogManager.getLogger(DateUtil.class);

	/**
	 * This is the Date pattern MongoDB follows
	 */
	public static final String MONGODB_DATETIME_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";
	
	public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	
	public static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm'Z'";
	
	public static final String DATE_PATTERN_1 = "yyyy/MM/dd";

	public static String formatDate(Date date) {
		return formatDate(date, DATE_PATTERN_1);
	}
	
	public static String formatDate(Date date, String format) {
		String parsedDate = "";
		if(date != null) {
			parsedDate = new SimpleDateFormat(format).format(date);				
		}
		return parsedDate;
	}
	
	public static Date getDate(String stringDate) throws ShopException{
		return getDate(stringDate, MONGODB_DATETIME_FORMAT);
	}
	
	/**
	 * Retrieve the java.util.Date value from a date string to be parsed in a given date pattern
	 * @param stringDate
	 * @param pattern
	 * @return
	 * @throws ShopException
	 */
	public static Date getDate(String stringDate, String pattern) throws ShopException {
		Date parsedDate = null;
		if(stringDate != null && stringDate != "") {
			if(pattern.equals(ISO_8601_FORMAT)){
				ZonedDateTime zdt = ZonedDateTime.parse(stringDate, DateTimeFormatter.ISO_DATE_TIME);
				parsedDate = Date.from(zdt.toInstant());
			} else {
				SimpleDateFormat formatter = new SimpleDateFormat(pattern);
			    try
			    {
			    	parsedDate = formatter.parse(stringDate);		    	
			    } catch (ParseException e) {
			      logger.error(e.getMessage());
			      throw new ShopException(AppConstant.DATE_ERROR);
			    }
			}
		}
		return parsedDate;
	}
	
	/**
	 * Check if a given date value matches a given date format 
	 * @param dateToValidate
	 * @param dateFormat
	 * @return
	 */
	public static boolean isThisDateValid(String dateToValidate, String dateFormat){
		boolean result = false;
		if(dateToValidate != null){
			if(dateFormat.equals(ISO_8601_FORMAT)){
				try {
		            DateTimeFormatter.ISO_DATE_TIME.parse(dateToValidate);
		            logger.debug("Matching date format is " + dateFormat);
		            result = true;
		        } catch (DateTimeParseException e) {
		        	logger.debug("Unable to format using " + dateFormat);
					logger.debug(e.getMessage());
		        	result = false;
		        }
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
				sdf.setLenient(false);
				try {
					//if not valid, it will throw ParseException
					sdf.parse(dateToValidate);
					logger.debug("Matching date format is " + dateFormat);
					result = true;
				} catch (ParseException e) {
					logger.debug("Unable to format using " + dateFormat);
					logger.debug(e.getMessage());
					result = false;
				}
			}
		}
		return result;
	}
	
}
