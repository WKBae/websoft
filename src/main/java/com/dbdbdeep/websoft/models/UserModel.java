package com.dbdbdeep.websoft.models;

public class UserModel {
	
	public static UserModel get(int id) {
		// select .. from user where id=id
		// return new UserModel / return null
		return new UserModel(id);
	}
	
	public static UserModel getUser(String username) {
		// select .. from user where username=username
		// return new UserModel(id);
	}
	
	public static UserModel create(String username, String password, String name, String email, boolean isAdmin) {
		// insert into user values (...)
		// return new UserModel(id);
	}
	
	private final int id;
	
	private UserModel(int id) {
		this.id = id;
	}
	
	public String getUsername() {
		// username = select username from user where username=username
		// return username;
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
