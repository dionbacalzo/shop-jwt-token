package com.shop.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.shop.domain.UserDomainObject;

@Repository
public interface UserDao extends MongoRepository<UserDomainObject, String>, UserDaoCustom {
	
	public UserDomainObject findByUserName(String userName);
	
	@Query("{ 'tryCounter' : { $gte: ?0 } }")
	public List<UserDomainObject> findByTryCounter(int tryCounter);

}
