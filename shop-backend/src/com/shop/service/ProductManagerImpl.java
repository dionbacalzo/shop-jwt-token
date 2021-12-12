package com.shop.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import com.shop.adapter.FileContentAdapter;
import com.shop.adapter.ShopItemAdapter;
import com.shop.constant.AppConstant;
import com.shop.dao.ProductDao;
import com.shop.domain.ItemDomainObject;
import com.shop.dto.InventoryItem;
import com.shop.exception.ShopException;
import com.shop.util.DateUtil;

@Service
@Component("productManagerImpl")
public class ProductManagerImpl implements ProductManager {
	
	protected final Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	private ProductDao productDaoImpl;
	
	public ProductManagerImpl(ProductDao productDaoImpl) {
		this.productDaoImpl = productDaoImpl;
	}
	/**
	 * returns a list of products structured in a specific format
	 */
	@Override
	public Map<String, Object> viewAll() {
		return ShopItemAdapter.parseItem(productDaoImpl.findAll());
	}
	/**
	 * returns a list of products contained in a InventoryItem object,
	 * Use this method instead of viewAll() for a more flexible way to manipulate the products
	 */
	@Override
	public List<InventoryItem> viewAllUnparsed() {
		return ShopItemAdapter.parseItemRaw(productDaoImpl.findAll());
	}
	
	/**
	 * return items made by a specific manufacturer
	 */
	@Override
	public Map<String, Object> searchByManufacturer(String manufacturer) {
		List<ItemDomainObject> itemList = productDaoImpl.findByManufacturer(manufacturer);
		return ShopItemAdapter.parseItem(itemList);
	}

	/**
	 * Update, insert or remove items
	 * return a new list of all items after a successful transaction
	 */
	@Override
	public List<InventoryItem> saveAll(List<InventoryItem> addList) {
		logger.debug(AppConstant.METHOD_IN);		
		List<ItemDomainObject> newList = null;
		try {
			List<ItemDomainObject> oldList = productDaoImpl.findAll();
			List<ItemDomainObject> currentList = ShopItemAdapter.parseItemInventory(addList);
			List<ItemDomainObject> deleteList = new ArrayList<ItemDomainObject>(oldList);
			// remove all items not found anymore in the new list
			deleteList.removeAll(currentList);			
			productDaoImpl.deleteAll(deleteList);
			// insert or update the products
			newList = productDaoImpl.saveAll(currentList);
		} catch (DuplicateKeyException e) {
			logger.error("Duplicate item " + e.getMessage());
	    } catch (MongoException e) {
	    	logger.error(e.getMessage());
	    } catch (ShopException e) {
	    	logger.error(e.getMessage());
	    	throw e;
		};
		
		logger.debug(AppConstant.METHOD_OUT);
		return ShopItemAdapter.parseItemRaw(newList);		
	}
	
	/**
	 * insert all products into record
	 */
	@Override
	public List<ItemDomainObject> insertAll(List<InventoryItem> addList) {
		logger.debug(AppConstant.METHOD_IN);		
		List<ItemDomainObject> itemList = null;
		try {
			itemList = productDaoImpl.insert(ShopItemAdapter.parseItemInventory(addList));
		} catch (DuplicateKeyException e) {
			logger.error("Duplicate item " + e.getMessage());
	    } catch (MongoException e) {
	    	logger.error(e.getMessage());
	    } catch (ShopException e) {
	    	logger.error(e.getMessage());
	    	throw e;
		};
		
		logger.debug(AppConstant.METHOD_OUT);
		return itemList;		
	}

	/**
	 * save all products found inside the file
	 */
	@Override
	public void saveAll(MultipartFile file) throws Exception {
		logger.debug(AppConstant.METHOD_IN);
		
		List<ItemDomainObject> itemList = null;
		try {
			
			FileContentAdapter fileContentAdapter = new FileContentAdapter(); 
			
			itemList = fileContentAdapter.parseFile(file, productDaoImpl.findAll());
			logger.info(new ObjectMapper().writeValueAsString(itemList));
			productDaoImpl.saveAll(itemList);
			
		} catch (DuplicateKeyException e) {
			logger.error("Duplicate item " + e.getMessage());
			throw(e);
	    } catch (MongoException e) {
	    	logger.error(e.getMessage());
	    	throw(e);
	    };
		
		logger.debug(AppConstant.METHOD_OUT);
	}

	/**
	 * delete an item
	 */
	@Override
	public void delete(InventoryItem item) throws ShopException {
		logger.debug(AppConstant.METHOD_IN);
		
		//delete by id
		if(!StringUtils.isEmpty(item.getId())) {
			if(productDaoImpl.existsById(item.getId())){
				productDaoImpl.deleteById(item.getId());
			} else {
				logger.error(MessageFormat.format(AppConstant.SHOP_ITEM_DELETE_NOT_FOUND, "id"));
				throw new ShopException(AppConstant.SHOP_ITEM_DELETE_FAIL);
			}
		//delete by title and release date	
		} else if(!StringUtils.isEmpty(item.getTitle()) && !StringUtils.isEmpty(item.getReleaseDate())) {
				Date formattedDate = DateUtil.getDate(item.getReleaseDate(), DateUtil.DATE_PATTERN_1);
			if(productDaoImpl.existsByTitleAndReleaseDate(item.getTitle(), formattedDate)){	
				productDaoImpl.deleteByTitleAndReleaseDate(item.getTitle(), formattedDate );
			} else {
				logger.error(MessageFormat.format(AppConstant.SHOP_ITEM_DELETE_NOT_FOUND, "Title and Release Date"));
				throw new ShopException(AppConstant.SHOP_ITEM_DELETE_FAIL);
			}
		} else {
			logger.error(AppConstant.SHOP_ITEM_DELETE_MISSING_REQUIRED);
			throw new ShopException(AppConstant.SHOP_ITEM_DELETE_FAIL);
		}
		
		logger.debug(AppConstant.METHOD_OUT);
	}

}
