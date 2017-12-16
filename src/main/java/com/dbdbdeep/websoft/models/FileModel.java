package com.dbdbdeep.websoft.models;

import java.io.InputStream;
import java.sql.*;
import java.util.Date;


public class FileModel {
	
	public static void createTable() throws SQLException {
		try(Connection conn = Database.getDatabase().getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS file (" +
							"id INT NOT NULL AUTO_INCREMENT," +
							"parent INT NOT NULL," +
							"file_name VARCHAR(100) NOT NULL," +
							"owner INT NOT NULL," +
							"upload_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ," +
							"contents BLOB NOT NULL," +
							"PRIMARY KEY (id)," +
							"FOREIGN KEY (parent) REFERENCES folder(id) ON DELETE CASCADE ," +
							"FOREIGN KEY (owener) REFERENCES user(id) ON DELETE CASCADE," +
							"UNIQUE (parent, file_name)" +
							");"
			);
			stmt.execute();
		}
	}
	
	public static FileModel get(int id) throws SQLException {
		Database db = Database.getDatabase();
		Object idColumn = db.selectSingleColumn("SELECT id FROM file WHERE id=?", id);
		if(idColumn == null) return null;
		else return new FileModel(id);
	}
	static FileModel getUnchecked(int id) {
		return new FileModel(id);
	}
	
	public static FileModel getFile(int parent, String fileName) throws SQLException {
		Database db = Database.getDatabase();
		Object idColumn = db.selectColumns("SELECT parent, file_name FROM file WHERE parent=? AND file_name=?", parent, fileName);
		if(idColumn == null) return null;
		else return new FileModel((Integer) idColumn);
	}
	
	public static FileModel create(int parent, String fileName, int owner, Date uploadTime, byte[] contents) throws SQLException {
		Database db = Database.getDatabase();
		try(Connection conn = db.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO user (parent, file_name, owner, upload_time, contents) VALUES (?, ?, ?, ?, ?)",
					PreparedStatement.RETURN_GENERATED_KEYS
			);
			stmt.setInt(1, parent);
			stmt.setString(2, fileName);
			stmt.setInt(3, owner);
			stmt.setTimestamp(4, new Timestamp(uploadTime.getTime()));
			stmt.setBytes(5, contents);
			
			int updatedRows = stmt.executeUpdate();
			if(updatedRows < 1) return null;
			
			try(ResultSet rs = stmt.getGeneratedKeys()) {
				if(rs.next()) {
					int id = rs.getInt(1);
					return new FileModel(id);
				} else {
					return null;
				}
			}
		}
	}
	
	private final int id;
	
	private FileModel(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public FolderModel getParent() throws SQLException {
		Integer parentId = (Integer) Database.getDatabase().selectSingleColumn("SELECT parent FROM file WHERE id=?", this.id);
		return (parentId == null)? null : FolderModel.get(parentId);
	}
	public void setParent(FolderModel parent) throws SQLException {
		Database.getDatabase().update("UPDATE file SET parent=? WHERE id=?", parent.getId(), this.id);
	}
	
	public String getFileName() throws SQLException {
		return (String) Database.getDatabase().selectSingleColumn("SELECT file_name FROM file WHERE id=?", this.id);
	}
	public void setFileName(String fileName) throws SQLException {
		Database.getDatabase().update("UPDATE file SET file_name=? WHERE id=?", fileName, this.id);
	}
	
	public int getOwner() throws SQLException {
		return (Integer) Database.getDatabase().selectSingleColumn("SELECT owner FROM file WHERE id=?", this.id);
	}
	public void setOwner(int owner) throws SQLException {
		Database.getDatabase().update("UPDATE file SET owner=? WHERE id=?", owner, this.id);
	}
	
	public Date getUploadTime() throws SQLException {
		Timestamp upload = (Timestamp) Database.getDatabase().selectSingleColumn("SELECT upload_time FROM file WHERE id=?", this.id);
		return new Date(upload.getTime());
	}
	public void setUploadTime(Date uploadTime) throws SQLException {
		Database.getDatabase().update("UPDATE file SET upload_time=? WHERE id=?", new Timestamp(uploadTime.getTime()), this.id);
	}
	
	public Blob getContents() throws SQLException {
		return (Blob) Database.getDatabase().selectSingleColumn("SELECT contents FROM file WHERE id=?", this.id);
	}
	public void setContents(byte[] contents) throws SQLException {
		Database db = Database.getDatabase();
		try(Connection conn = db.getConnection()) {
			Blob b = conn.createBlob();
			b.setBytes(1, contents);
			try(PreparedStatement stmt = conn.prepareStatement("UPDATE file SET contents=? WHERE id=?")) {
				stmt.setBlob(1, b);
				stmt.setInt(2, this.id);
			}
		}
	}
	public void setContents(InputStream stream) throws SQLException {
		Database db = Database.getDatabase();
		try(Connection conn = db.getConnection();
		    PreparedStatement stmt = conn.prepareStatement("UPDATE file SET contents=? WHERE id=?")) {
			stmt.setBlob(1, stream);
			stmt.setInt(2, this.id);
		}
	}
	
}

