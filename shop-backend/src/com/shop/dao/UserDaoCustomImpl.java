package com.shop.dao;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.client.result.UpdateResult;
import com.shop.constant.AppConstant;
import com.shop.domain.UserDomainObject;
import com.shop.dto.Result;
import com.shop.dto.User;

public class UserDaoCustomImpl implements UserDaoCustom {

	protected final Logger logger = LogManager.getLogger(getClass());

	private final MongoTemplate mongoTemplate;

	@Autowired
	public UserDaoCustomImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	/**
	 * Updates details of a User that matches the given username
	 * @return UserDomainObject returns a null object if update is unsuccessful
	 */
	@Override
	public UserDomainObject updateByUserName(String username, User user) {
		logger.debug(AppConstant.METHOD_IN);

		UserDomainObject userDomainObj = null;
		try {
			final Query query = new Query();
			query.addCriteria(Criteria.where("userName").is(username));

			Update update = new Update();
			update.set("firstname", user.getFirstname());
			update.set("lastname", user.getLastname());
			//picture is optional
			if (user.getPicture() != null) {
				update.set("picture", new Binary(BsonBinarySubType.BINARY, user.getPicture()));
			}

			UpdateResult updateResult = mongoTemplate.updateFirst(query, update, UserDomainObject.class);

			if (updateResult.wasAcknowledged()) {
				userDomainObj = mongoTemplate.findOne(query, UserDomainObject.class);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		logger.debug(AppConstant.METHOD_OUT);
		return userDomainObj;
	}

	/**
	 * Update the password of a user that matches a given username
	 */
	@Override
	public Result updatePasswordByUserName(String username, String newPassword) {
		logger.debug(AppConstant.METHOD_IN);

		Result result = new Result();

		final Query query = new Query();
		query.addCriteria(Criteria.where("userName").is(username));

		Update update = new Update();
		update.set("password", newPassword);

		UpdateResult updateResult = mongoTemplate.updateFirst(query, update, UserDomainObject.class);

		if (updateResult.wasAcknowledged()) {
			result = new Result(AppConstant.SHOP_PASSWORD_UPDATE_SUCCESSFUL_STATUS,
					AppConstant.SHOP_PASSWORD_UPDATE_MESSAGE_SUCCESS);
		} else {
			result = new Result(AppConstant.SHOP_PASSWORD_UPDATE_UNSUCCESSFUL_STATUS,
					AppConstant.SHOP_PASSWORD_UPDATE_MESSAGE_FAIL);
		}

		logger.debug(AppConstant.METHOD_OUT);
		return result;
	}

}
