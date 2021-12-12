package com.shop.dto;

import java.util.Map;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ShopContentPage extends ResourceSupport {

    private Map<String, Object> content;

    @JsonCreator
    public ShopContentPage(@JsonProperty("content") Map<String, Object> content) {
        this.content = content;
    }

    public Map<String, Object> getContent() {
        return content;
    }
    /*
    @Override
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("_link")
    public List<Link> getLinks() {
    	// TODO Auto-generated method stub
    	return super.getLinks();
    }*/

}
