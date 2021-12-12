package com.shop.controller;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.constant.AppConstant;
import com.shop.dto.InventoryItem;
import com.shop.dto.Item;
import com.shop.dto.Result;
import com.shop.dto.ShopContentPage;
import com.shop.service.ProductManager;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true") //allow CORS for angular
@RestController
public class ShopController {
	
	private final Logger logger = LogManager.getLogger(getClass());
	
	@Autowired @Qualifier("productManagerImpl")	
	private ProductManager productManagerImpl;
	
	@RequestMapping(value = "")
	protected ModelAndView viewHomePage() throws Exception {
		logger.debug(AppConstant.METHOD_IN);
		
		ModelAndView model = new ModelAndView("content");
		
		logger.debug(AppConstant.METHOD_OUT);
		return model;
	}
	
	@RequestMapping(value = "/content")
	protected ModelAndView viewContentPage() throws Exception {
		logger.debug(AppConstant.METHOD_IN);

		ModelAndView model = new ModelAndView("content");
		
		logger.debug(AppConstant.METHOD_OUT);
		return model;
	}
	
	@RequestMapping(value = "/upload")
	protected ModelAndView viewUploadPage() throws Exception {
		logger.debug(AppConstant.METHOD_IN);

		ModelAndView model = new ModelAndView("upload");
		
		logger.debug(AppConstant.METHOD_OUT);
		return model;
	}
	
	@RequestMapping(value = "/viewList")
	public String viewList() {
		logger.debug(AppConstant.METHOD_IN);
		
		String json = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map<String, Object> items = new HashMap<String, Object>();
			items.put("items", productManagerImpl.viewAll());
			json = mapper.writeValueAsString(items);
		} catch (JsonProcessingException e) {
			logger.error(e);
		}
		
		logger.debug(AppConstant.METHOD_OUT);
		return json;
	}
	
	@RequestMapping(value = "/manufacturer")
	public String viewManufacturerItems(@RequestBody Item searchQuery) {
		logger.debug(AppConstant.METHOD_IN);
		
		String json = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			json = mapper.writeValueAsString(productManagerImpl.searchByManufacturer(searchQuery.getManufacturer()));
		} catch (JsonProcessingException e) {
			logger.error(e);
		}
		
		logger.debug(AppConstant.METHOD_OUT);
		return json;
	}
	
	@RequestMapping(value = "/save")
	public String saveItems(@RequestBody List<InventoryItem> itemList) {
		logger.debug(AppConstant.METHOD_IN);
		
		String json = "";
		try {
			Map<String, Object> items = new HashMap<String, Object>();
			items.put("itemList", productManagerImpl.saveAll(itemList));
			json = new ObjectMapper().writeValueAsString(items);
		} catch (JsonProcessingException e) {
			logger.error(e);
		}
		
		logger.debug(AppConstant.METHOD_OUT);
		return json;		
	}
	
	@RequestMapping(value = "/addItems")
	public String insertItems(@RequestBody List<InventoryItem> itemList) {
		logger.debug(AppConstant.METHOD_IN);
		
		String json = "";
		try {
			json = new ObjectMapper().writeValueAsString(productManagerImpl.insertAll(itemList));
		} catch (JsonProcessingException e) {
			logger.error(e);
		}
		
		logger.debug(AppConstant.METHOD_OUT);
		return json;		
	}
	
	@RequestMapping(value = "/viewPagedList")
	public HttpEntity<ShopContentPage> viewPagedList() {
		logger.debug(AppConstant.METHOD_IN);
		
		ShopContentPage shopContentPage = new ShopContentPage(productManagerImpl.viewAll());
		shopContentPage.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(ShopController.class).viewPagedList()).withSelfRel());

		logger.debug(AppConstant.METHOD_OUT);
		
		return new ResponseEntity<ShopContentPage> (shopContentPage, HttpStatus.OK);
	}
	
	
	
	@RequestMapping(value = "/viewListUnparsed")
	public String viewListRaw() {
		logger.debug(AppConstant.METHOD_IN);
		
		String json = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map<String, Object> items = new HashMap<String, Object>();
			items.put("itemList", productManagerImpl.viewAllUnparsed());
			json = mapper.writeValueAsString(items);
		} catch (JsonProcessingException e) {
			logger.error(e);
		}
		
		logger.debug(AppConstant.METHOD_OUT);
		return json;
	}
	
	@RequestMapping(value = "uploadItems")
	@ResponseBody
	public String uploadItems(@RequestParam("file") MultipartFile file) {
		logger.debug(AppConstant.METHOD_IN);
		
		Result result = new Result();
		String message = "";
		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		if (file != null && !file.isEmpty()) {
			try {
				File shopFile = new File(file.getOriginalFilename()); //get the filename, works for all browsers especially IE/Edge
				
				productManagerImpl.saveAll(file);
				
				message = MessageFormat.format(AppConstant.SHOP_ITEM_UPLOAD_SUCCESS, shopFile.getName());
				result = new Result(AppConstant.SHOP_ITEM_UPLOAD_SUCCESSFUL_STATUS, message);				
			} catch (Exception e) {
				logger.error(e);
				message = AppConstant.SHOP_ITEM_UPLOAD_FAIL_MESSAGE;
				result = new Result(AppConstant.SHOP_ITEM_UPLOAD_UNSUCCESSFUL_STATUS, message);
			}
		} else {
			message = AppConstant.SHOP_ITEM_FILE_EMPTY;
			result = new Result(AppConstant.SHOP_ITEM_UPLOAD_UNSUCCESSFUL_STATUS, message);
		}
		
		try {
			json = mapper.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			logger.error(e);
		}
		
		logger.debug(AppConstant.METHOD_OUT);
		return json;
	}
	
	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(@RequestBody InventoryItem item) {
		logger.debug(AppConstant.METHOD_IN);
		
		String message = "";
		if (!StringUtils.isEmpty(item)) {
			try {
				
				productManagerImpl.delete(item);
				
				message = AppConstant.SHOP_ITEM_DELETE_SUCCESS;
			} catch (Exception e) {
				logger.error(e);
				message = AppConstant.SHOP_ITEM_DELETE_FAIL;
			}
		} else {
			message = AppConstant.SHOP_ITEM_DELETE_FAIL;
		}
		
		logger.debug(AppConstant.METHOD_OUT);
		return message;
	}
	
}
