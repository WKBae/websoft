package com.dbdbdeep.websoft.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserModel {
	
	public static UserModel get(int id) throws SQLException {
		Database db = Database.getDatabase();
		Object idColumn = db.selectSingleColumn("SELECT id FROM user WHERE id=?", id);
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
		try(Connection conn = db.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO user (username, password, name, email, is_admin) VALUES (?, ?, ?, ?, ?)",
					PreparedStatement.RETURN_GENERATED_KEYS
			);
			stmt.setString(1, username);
			stmt.setString(2, password);
			stmt.setString(3, name);
			stmt.setString(4, email);
			stmt.setBoolean(5, isAdmin);
			
			int updatedRows = stmt.executeUpdate();
			if(updatedRows < 1) return null;
			
			try(ResultSet rs = stmt.getGeneratedKeys()) {
				if(rs.next()) {
					int id = rs.getInt(1);
					return new UserModel(id);
				} else {
					return null;
				}
			}
		}
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
	
	public String getPassword() throws SQLException {
		return (String) Database.getDatabase().selectSingleColumn("SELECT password FROM user WHERE id=?", this.id);
	}
	
	public void setPassword(String password) throws SQLException {
		Database.getDatabase().update("UPDATE user SET password=? WHERE id=?", password, this.id);
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
