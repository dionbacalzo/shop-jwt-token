package com.shop.dto;

import org.springframework.stereotype.Component;

@Component
public class Container {
	
	public Container() {};
	
	public Container(String parent, String title) {
		this.parent = parent;
		this.title = title;
	}
	
	private String parent; 
	
	private String title;

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;	
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
        if (!(obj instanceof Container)) {
            return false;
        }

        Container container = (Container) obj;

		return this.title.equals(container.title) && this.parent.equals(container.parent);
	}
	
	@Override
    public int hashCode() {
		int result = 17;
		result = 31 * result + title.hashCode();
		result = 31 * result + parent.hashCode();
		return result;
    }
}
