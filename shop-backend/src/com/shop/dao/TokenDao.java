package com.shop.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.shop.domain.TokenDomainObject;

@Repository
public interface TokenDao extends MongoRepository<TokenDomainObject, String> {
	
	List<TokenDomainObject> findByUserLogin(String userLogin);

}
