package com.dbdbdeep.websoft.models;

public class UserModel {
	
	public static UserModel get(String id) {
		// select .. from user where id=id
		// return new UserModel / return null
		return new UserModel(id);
	}
	
	public static UserModel create(String id, String password, String name, String email, boolean isAdmin) {
		// insert into user values (...)
		return new UserModel(id);
	}
	
	private final String id;
	
	private UserModel(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public String getPassword() {
		// password = select password from user where id=id
		// return password;
	}
	
	public void setPassword(String password) {
		// update user set password=? where id=id
	}
	
	public String getName() {
		// name = select name from user where id=id
		// return name;
	}
	
	public void setName(String name) {
		// update user set name=? where id=id
	}
	
	public String getEmail() {
		// email = select email from user where id=id
		// return email;
	}
	
	public void setEmail(String email) {
		// update user set email=? where id=id
	}
	
	public boolean isAdmin() {
		// isAdmin = select is_admin from user where id=id
		// return isAdmin;
	}
	
	public void setAdmin(boolean admin) {
		// update user set is_admin=? where id=id
	}
	
}
