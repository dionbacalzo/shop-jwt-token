package com.shop.dto;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component
public class Item extends Container {
	
	public Item() {
		
	}
	
	public Item(String manufacturer, BigDecimal price, String releaseDate) {
		this.manufacturer = manufacturer;
		this.price = price;
		this.releaseDate = releaseDate;		
	}
	
	private String manufacturer;
	
	private BigDecimal price;
	
	private String releaseDate;

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}
	
}
