package com.shop.dto;

import java.math.BigDecimal;

import org.springframework.util.StringUtils;

/**
 * 
 * @author Dionisio.Bacalzo
 *
 * RequestBody to add to shop database
 *
 */
public class InventoryItem {
	
	private String id;

	private String title;
	
	private String manufacturer;
	
	private BigDecimal price;
	
	private String releaseDate;
	
	private String type;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
        if (!(obj instanceof InventoryItem)) {
            return false;
        }

        InventoryItem item = (InventoryItem) obj;
        if (!StringUtils.isEmpty(this.id) && !StringUtils.isEmpty(item.id)) {
        	return this.id.equals(item.id);
        }
		return this.title.equals(item.title) && this.releaseDate.equals(item.releaseDate);
	}
	
	@Override
    public int hashCode() {
		int result = 17;
		if(!StringUtils.isEmpty(this.id)) {
			result = 31 * result + id.hashCode();
		} else {
			result = 31 * result + title.hashCode();
			result = 31 * result + releaseDate.hashCode();
		}
		return result;
    }
	
}
