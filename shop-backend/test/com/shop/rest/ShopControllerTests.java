package com.shop.rest;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.shop.config.TestConfig;
import com.shop.controller.ShopController;
import com.shop.service.ProductManager;

import junit.framework.TestCase;


@ContextConfiguration(classes= {TestConfig.class})
@RunWith(SpringRunner.class)
@WebMvcTest(properties= "spring.main.allow-bean-definition-overriding=true",value = ShopController.class, secure = true)
public class ShopControllerTests extends TestCase {
	
	@Autowired
	private MockMvc mockMvc;

	@Mock //@Qualifier("productManagerImpl")
    private ProductManager productManagerImpl;
 
	@Test
	public void testViewListConnection() throws Exception {
		//Item item = new Item("", new BigDecimal(0.0), "");
		//Container category = new Container("", "");
		//ShopContent shopContent = new ShopContent(new HashSet<Container>(Arrays.asList(category)), new ArrayList<Item>(Arrays.asList(item)));
		
		Mockito.when(productManagerImpl.viewAll()).thenReturn(new HashMap<String, Object>());
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post("/viewList")
				.accept(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();
		
		//test connections
		assertEquals(HttpStatus.OK.value(), response.getStatus());
	}
}
