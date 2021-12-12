package com.shop.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import com.shop.dao.ProductDao;
import com.shop.domain.ItemDomainObject;

import junit.framework.TestCase;

@RunWith(SpringRunner.class)
public class ProductManagerImplTests extends TestCase {
	
    @Mock
	private ProductDao productDaoImpl;
    	
	private ProductManager productManagerImpl;
	
	private ItemDomainObject phoneProduct;
	
	private ItemDomainObject laptopProduct;
	
	private ItemDomainObject accessoryProduct;

	@Before
	public void init() {
		productManagerImpl = new ProductManagerImpl(productDaoImpl);
		
		phoneProduct = new ItemDomainObject();		
		phoneProduct.setManufacturer("Apple");
		phoneProduct.setPrice(new BigDecimal(2100));
		phoneProduct.setReleaseDate(new Date());
		phoneProduct.setTitle("Iphone 5S");
		phoneProduct.setType("phone");
		
		laptopProduct = new ItemDomainObject();
		laptopProduct.setManufacturer("dell");
		laptopProduct.setPrice(new BigDecimal(1100));
		laptopProduct.setReleaseDate(new Date());
		laptopProduct.setTitle("I5 gaming");
		laptopProduct.setType("laptop");
		
		accessoryProduct = new ItemDomainObject();
		accessoryProduct.setManufacturer("Leitner");
		accessoryProduct.setPrice(new BigDecimal(100));
		accessoryProduct.setReleaseDate(new Date());
		accessoryProduct.setTitle("headset");
		accessoryProduct.setType("accessory");
	}
	
	@Test
	public void testParseItemProductContent() {
		List<ItemDomainObject> productList = new ArrayList<ItemDomainObject>();
		
		productList.add(phoneProduct);
		
		Mockito.when(productDaoImpl.findAll()).thenReturn(productList);
		
		Map<String, Object> shopContent = productManagerImpl.viewAll();
		
		assertNotNull(shopContent);
		
		//assertNotNull(shopContent.g);
		
		//title content
		//assertEquals(shopContent.getProducts().get(0).getManufacturer(), "Apple");
		
		//price content
		//assertEquals(shopContent.getProducts().get(0).getPrice(), new BigDecimal("29700"));
	}
	
}
