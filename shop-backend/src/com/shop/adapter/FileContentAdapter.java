package com.shop.adapter;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.web.multipart.MultipartFile;

import com.shop.constant.AppConstant;
import com.shop.domain.ItemDomainObject;
import com.shop.exception.ShopException;
import com.shop.util.DateUtil;
import com.shop.util.ShopUtil;

public class FileContentAdapter {
	
	protected final Logger logger = LogManager.getLogger(getClass());
	
	public List<ItemDomainObject> parseFile(String fileContent, List<ItemDomainObject> list) throws Exception{
		logger.debug(AppConstant.METHOD_IN);
		
		List<ItemDomainObject> itemList = new ArrayList<ItemDomainObject>();
		
		try {
			
			int rowNumber = 1;
			for (String line : fileContent.split("[\\r\\n]+")){
				
				ItemDomainObject item = new ItemDomainObject();
				
				//Split a string by commas but ignore commas within double-quotes
				String[] lineContentList = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
				
				if (lineContentList.length != AppConstant.TOTAL_SHOP_ITEM_SIZE) {
					throw new ShopException(MessageFormat.format(AppConstant.SHOP_ITEM_ROW_SIZE_ERROR, AppConstant.TOTAL_SHOP_ITEM_SIZE, rowNumber));
				}
				
				//add title
				if(!lineContentList[0].isEmpty()) {
					item.setTitle(lineContentList[0].trim());
				} else {
					throw new ShopException(MessageFormat.format(AppConstant.SHOP_ITEM_REQUIRED, "title", rowNumber));
				}
				
				//add price
				if(!lineContentList[1].isEmpty()) {
					item.setPrice(ShopUtil.formatPrice(lineContentList[1].trim()));
				} else {
					throw new ShopException(MessageFormat.format(AppConstant.SHOP_ITEM_REQUIRED, "price", rowNumber));
				}
				
				//add type
				if(!lineContentList[2].isEmpty()) {
					item.setType(lineContentList[2].trim());
				} else {
					throw new ShopException(MessageFormat.format(AppConstant.SHOP_ITEM_REQUIRED, "type", rowNumber));
				}
				
				//add manufacturer
				if(!lineContentList[3].isEmpty()) {
					item.setManufacturer(lineContentList[3].trim());
				} else {
					throw new ShopException(MessageFormat.format(AppConstant.SHOP_ITEM_REQUIRED, "manufacturer", rowNumber));
				}
				
				//add Release Date
				if(!lineContentList[4].isEmpty()) {
					item.setReleaseDate(DateUtil.getDate(lineContentList[4].trim(), DateUtil.DEFAULT_DATETIME_FORMAT));
				} else {
					throw new ShopException(MessageFormat.format(AppConstant.SHOP_ITEM_REQUIRED, "release date", rowNumber));
				}
				
				//If the document contains an _id field, 
				//then the save() method is equivalent to an update with the upsert option set to true and the query predicate on the _id field.
				if(list.contains(item)){
					item.set_id(list.get(list.indexOf(item)).get_id());
					logger.debug("updating existing item " + item.getTitle());
				}
				
				
				itemList.add(item);
			}
			
		} catch (NumberFormatException e) {
			logger.error("Unable to parse Integer" + e.getMessage());
			throw(new NumberFormatException("Parsing file error for integer value"));
		} catch (Exception e) {
			logger.error("Parsing file Error" + e.getMessage());
			throw(e);
		}
		
		logger.debug(AppConstant.METHOD_OUT);
		
		return itemList;
	}
	
	public List<ItemDomainObject> parseFile(MultipartFile file, List<ItemDomainObject> list) throws Exception{
		logger.debug(AppConstant.METHOD_IN);
		
		List<ItemDomainObject> shopList = new ArrayList<ItemDomainObject>();
		
		String fileContent;
		try {
			fileContent = new String(file.getBytes(), "UTF-8");
			
			shopList = parseFile(fileContent, list);
		} catch (IOException e) {
			logger.error("Parsing file Error: " + e.getMessage());
			throw(e);
		}
		
		logger.debug(AppConstant.METHOD_OUT);
		
		return shopList;
	}
	
}
