package com.shop.domain;

import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "UserDomainObject")
public class UserDomainObject {
	
	@Id
    private ObjectId _id;
	
	@Indexed(name = "userName", unique = true)
	private String userName;
	
	private String firstname;
	
	private String lastname;
	
	private String password;
	
	private String role;
	
	private int tryCounter;
		
    private Binary picture;

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getTryCounter() {
		return tryCounter;
	}

	public void setTryCounter(int tryCounter) {
		this.tryCounter = tryCounter;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Binary getPicture() {
		return picture;
	}

	public void setPicture(Binary picture) {
		this.picture = picture;
	}
}
