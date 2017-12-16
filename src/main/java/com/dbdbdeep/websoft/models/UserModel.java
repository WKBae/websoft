package com.dbdbdeep.websoft.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserModel {
	
	public static void createTable() throws SQLException {
		try(Connection conn = Database.getDatabase().getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS user (" +
							"id INT NOT NULL AUTO_INCREMENT," +
							"username VARCHAR(30) NOT NULL," +
							"password CHAR(41) NOT NULL," +
							"name VARCHAR(20) NOT NULL," +
							"email VARCHAR(50)," +
							"joined TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
							"is_admin TINYINT(1) DEFAULT 0 NOT NULL," +
							"PRIMARY KEY (id)," +
							"UNIQUE (username)" +
					");"
			);
			stmt.execute();
		}
	}
	
	public static UserModel get(int id) throws SQLException {
		Database db = Database.getDatabase();
		Object idColumn = db.selectSingleColumn("SELECT 1 FROM user WHERE id=?", id);
		if(idColumn == null) return null;
		else return new UserModel(id);
	}
	
	public static UserModel getUser(String username) throws SQLException {
		Database db = Database.getDatabase();
		Object idColumn = db.selectSingleColumn("SELECT id FROM user WHERE username=?", username);
		if(idColumn == null) return null;
		else return new UserModel((Integer) idColumn);
	}
	
	public static UserModel create(String username, String password, String name, String email, boolean isAdmin) throws SQLException {
		Database db = Database.getDatabase();
		Integer id = db.insertGetId(
				"INSERT INTO user (username, password, name, email, is_admin) VALUES (?, PASSWORD(?), ?, ?, ?)",
				username, password, name, email, isAdmin
		);
		return (id == null)? null : new UserModel(id);
	}
	
	private final int id;
	
	private UserModel(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public String getUsername() throws SQLException {
		return (String) Database.getDatabase().selectSingleColumn("SELECT username FROM user WHERE id=?", this.id);
	}
	
	public void setUsername(String username) throws SQLException {
		Database.getDatabase().update("UPDATE user SET username=? WHERE id=?", username, this.id);
	}
	
	public boolean checkPassword(String password) throws SQLException {
		Boolean result = Database.getDatabase().selectSingleColumnAs(Boolean.class, "SELECT password=PASSWORD(?) FROM user WHERE id=?", password, this.id);
		return Boolean.TRUE.equals(result);
	}
	
	public void setPassword(String password) throws SQLException {
		Database.getDatabase().update("UPDATE user SET password=PASSWORD(?) WHERE id=?", password, this.id);
	}
	
	public String getName() throws SQLException {
		return (String) Database.getDatabase().selectSingleColumn("SELECT name FROM user WHERE id=?", this.id);
	}
	
	public void setName(String name) throws SQLException {
		Database.getDatabase().update("UPDATE user SET name=? WHERE id=?", name, this.id);
	}
	
	public String getEmail() throws SQLException {
		return (String) Database.getDatabase().selectSingleColumn("SELECT email FROM user WHERE id=?", this.id);
	}
	
	public void setEmail(String email) throws SQLException {
		Database.getDatabase().update("UPDATE user SET email=? WHERE id=?", email, this.id);
	}
	
	public boolean isAdmin() throws SQLException {
		return Boolean.TRUE.equals(Database.getDatabase().selectSingleColumn("SELECT is_admin FROM user WHERE id=?", this.id));
	}
	
	public void setAdmin(boolean admin) throws SQLException {
		Database.getDatabase().update("UPDATE user SET is_admin=? WHERE id=?", admin, this.id);
	}
}
