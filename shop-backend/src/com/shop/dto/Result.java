package com.shop.dto;

public class Result {
	
	public Result () { }
	
	public Result(String status, String message) {
		this.status = status;
		this.message = message;
	}

	private String status;
	
	private String message;
	
	/*
	 * Extra details such as user that logged in, etc.
	 */
	private Object Details;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getDetails() {
		return Details;
	}

	public void setDetails(Object details) {
		Details = details;
	}
	
}
