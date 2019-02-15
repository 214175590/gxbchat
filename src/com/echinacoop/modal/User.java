package com.echinacoop.modal;

public class User {

	private String name;
	private String userId;
	private int type;
	
	public User() {
	}
	
	public User(String n, String b) {
		name = n;
		userId = b;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String toString() {
		return name;
	}
}
