package com.shop.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.StringUtils;

@CompoundIndex(name = "unique_item", def = "{'title' : 1, 'releaseDate' : 1}", unique = true)
@Document(collection = "ItemDomainObject")
public class ItemDomainObject {
	
	@Id
    private ObjectId _id;
	
	@Field("title")
	private String title;
	
	private String manufacturer;
	
	private String type;
	
	private BigDecimal price;
	
	@Field("releaseDate")
	private Date releaseDate;

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
        if (!(obj instanceof ItemDomainObject)) {
            return false;
        }

        ItemDomainObject item = (ItemDomainObject) obj;
        if (!StringUtils.isEmpty(this._id) && !StringUtils.isEmpty(item._id)) {
        	return this._id.equals(item._id);
        }
		return this.title.equals(item.title) && this.releaseDate.equals(item.releaseDate);
	}
	
	@Override
    public int hashCode() {
		int result = 17;
		if(!StringUtils.isEmpty(this._id)) {
			result = 31 * result + _id.hashCode();
		} else {
			result = 31 * result + title.hashCode();
			result = 31 * result + releaseDate.hashCode();
		}
		return result;
    }
}
