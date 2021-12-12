package com.shop.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.shop.domain.ItemDomainObject;
import com.shop.dto.InventoryItem;
import com.shop.exception.ShopException;

@Service
public interface ProductManager {

	public Map<String, Object> viewAll();
	
	public List<InventoryItem> viewAllUnparsed();
	
	public Map<String, Object> searchByManufacturer(String manufacturer);
	
	public List<InventoryItem> saveAll(List<InventoryItem> shopList);
	
	public List<ItemDomainObject> insertAll(List<InventoryItem> addList);

	public void saveAll(MultipartFile file) throws Exception;

	public void delete(InventoryItem item) throws ShopException;

}
