package com.shop.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.shop.domain.ItemDomainObject;

@Repository
public interface ProductDao extends MongoRepository<ItemDomainObject, String> {
	
	public ItemDomainObject findByTitleAndReleaseDate(String title, String releaseDate);
	
	public List<ItemDomainObject> findByManufacturer(String manufacturer);
	
	public void deleteByTitleAndReleaseDate(String title, Date releaseDate);

	public boolean existsByTitleAndReleaseDate(String title, Date releaseDate);
	
}
