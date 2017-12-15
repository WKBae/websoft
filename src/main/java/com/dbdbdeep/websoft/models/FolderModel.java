package com.dbdbdeep.websoft.models;

import java.sql.*;
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
							"created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
							"modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
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
	
	public static FolderModel create(FolderModel parent, String name, UserModel owner, Date created, Date modified) throws SQLException {
		Database db = Database.getDatabase();
		Integer id = db.insertGetId(
				"INSERT INTO user (parent, name, owner, created, modified) VALUES (?, ?, ?, ?, ?)",
				parent != null? parent.getId() : null,
				name,
				owner != null? owner.getId() : null,
				new Timestamp(created.getTime()),
				new Timestamp(modified.getTime())
		);
		return (id == null)? null : new FolderModel(id);
	}
	
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
		Timestamp date = (Timestamp) Database.getDatabase().selectSingleColumn("SELECT created FROM folder WHERE id=?", this.id);
		return new Date(date.getTime());
	}
	public void setCreatedDate(Date created) throws SQLException {
		Database.getDatabase().update("UPDATE folder SET created=? WHERE id=?", new Timestamp(created.getTime()), this.id);
	}
	
	public Date getModifiedDate() throws SQLException {
		Timestamp date = (Timestamp) Database.getDatabase().selectSingleColumn("SELECT modified FROM folder WHERE id=?", this.id);
		return new Date(date.getTime());
	}
	public void setModifiedDate(Date modified) throws SQLException {
		Database.getDatabase().update("UPDATE folder SET modified=? WHERE id=?", new Timestamp(modified.getTime()), this.id);
	}
}
