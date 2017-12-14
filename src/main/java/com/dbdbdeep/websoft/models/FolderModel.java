package com.dbdbdeep.websoft.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class FolderModel {
	
	public static void createTable() throws SQLException {
		try(Connection conn = Database.getDatabase().getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(
					"CREATE TABLE folder (" +
							"id INT NOT NULL AUTO_INCREMENT," +
							"parent INT," +
							"name VARCHAR(100) NOT NULL," +
							"owner INT NOT NULL," +
							"created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
							"modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
							"PRIMARY KEY (id)," +
							"FOREIGN KEY (parent) REFERENCES folder(id)" +
								"ON DELETE CASCADE," +
							"FOREIGN KEY (owner) REFERENCES user(id)" +
								"ON DELETE CASCADE," +
							"UNIQUE (parent, name)" +
						");"
			);
			stmt.execute();
		}
	}
	
	public static FolderModel get(int id) throws SQLException {
		Database db = Database.getDatabase();
		Object idColumn = db.selectSingleColumn("SELECT 1 FROM folder WHERE id=?", id);
		if(idColumn == null) return null;
		else return new FolderModel(id);
	}
	
	public static FolderModel create()
	
	private final int id;
	
	private FolderModel(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public FolderModel getParent() throws SQLException {
		Integer parentId = (Integer) Database.getDatabase().selectSingleColumn("SELECT parent FROM folder WHERE id=?", this.id);
		if(parentId == null) {
			return null;
		} else {
			return FolderModel.get(parentId);
		}
	}
	public void setParent(FolderModel parent) throws SQLException {
		Integer parentId = parent != null? parent.getId() : null;
		Database.getDatabase().update("UPDATE folder SET parent=? WHERE id=?", parentId, this.id);
	}
	
	public String getName() throws SQLException {
		return (String) Database.getDatabase().selectSingleColumn("SELECT name FROM folder WHERE id=?", this.id);
	}
	public void setName(String name) throws SQLException {
		Database.getDatabase().update("UPDATE folder SET name=? WHERE id=?", name, this.id);
	}
	
	public UserModel getOwner() throws SQLException {
		int ownerId = (Integer) Database.getDatabase().selectSingleColumn("SELECT owner FROM folder WHERE id=?", this.id);
		return UserModel.get(ownerId);
	}
	public void setOwner(UserModel owner) throws SQLException {
		Database.getDatabase().update("UPDATE folder SET owner=? WHERE id=?", owner.getId(), this.id);
	}
	
	public Date getCreatedDate() throws SQLException {
		java.sql.Date date = (java.sql.Date) Database.getDatabase().selectSingleColumn("SELECT created FROM folder WHERE id=?", this.id);
		return new Date(date.getTime());
	}
	public void setCreatedDate(Date created) throws SQLException {
		Database.getDatabase().update("UPDATE folder SET created=? WHERE id=?", new java.sql.Date(created.getTime()), this.id);
	}
	
	public Date getModifiedDate() throws SQLException {
		java.sql.Date date = (java.sql.Date) Database.getDatabase().selectSingleColumn("SELECT modified FROM folder WHERE id=?", this.id);
		return new Date(date.getTime());
	}
	public void setModifiedDate(Date modified) throws SQLException {
		Database.getDatabase().update("UPDATE folder SET modified=? WHERE id=?", new java.sql.Date(modified.getTime()), this.id);
	}
}
