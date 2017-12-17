package com.dbdbdeep.websoft.models;

import java.io.IOException;
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
							"name VARCHAR(100) NOT NULL," +
							"owner INT NOT NULL," +
							"upload_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
							"content LONGBLOB NOT NULL," +
							"PRIMARY KEY (id)," +
							"FOREIGN KEY (parent) REFERENCES folder(id) ON DELETE CASCADE," +
							"FOREIGN KEY (owener) REFERENCES user(id) ON DELETE CASCADE," +
							"UNIQUE (parent, name)" +
							") DEFAULT CHARACTER SET utf8;"
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
	
	public static FileModel create(FolderModel parent, String fileName, UserModel owner, Date uploadTime, byte[] contents) throws SQLException {
		Database db = Database.getDatabase();
		try(Connection conn = db.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO file (parent, name, owner, upload_time, content) VALUES (?, ?, ?, ?, ?)",
					PreparedStatement.RETURN_GENERATED_KEYS
			);
			stmt.setInt(1, parent.getId());
			stmt.setString(2, fileName);
			stmt.setInt(3, owner.getId());
			stmt.setTimestamp(4, new Timestamp(uploadTime.getTime()));
			Blob b = conn.createBlob();
			b.setBytes(1, contents);
			stmt.setBlob(5, b);
			
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
	
	public static FileModel create(FolderModel parent, String fileName, UserModel owner, Date uploadTime, InputStream contents) throws SQLException {
		Database db = Database.getDatabase();
		try(Connection conn = db.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO file (parent, name, owner, upload_time, content) VALUES (?, ?, ?, ?, ?)",
					PreparedStatement.RETURN_GENERATED_KEYS
			);
			stmt.setInt(1, parent.getId());
			stmt.setString(2, fileName);
			stmt.setInt(3, owner.getId());
			stmt.setTimestamp(4, new Timestamp(uploadTime.getTime()));
			stmt.setBlob(5, contents);
			
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
	
	public void delete() throws SQLException {
		Database.getDatabase().update("DELETE FROM file WHERE id=?", this.id);
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
	
	public String getName() throws SQLException {
		return (String) Database.getDatabase().selectSingleColumn("SELECT name FROM file WHERE id=?", this.id);
	}
	
	public void setName(String fileName) throws SQLException {
		Database.getDatabase().update("UPDATE file SET name=? WHERE id=?", fileName, this.id);
	}
	
	public UserModel getOwner() throws SQLException {
		Integer ownerId = (Integer) Database.getDatabase().selectSingleColumn("SELECT owner FROM file WHERE id=?", this.id);
		return UserModel.get(ownerId);
	}
	public void setOwner(UserModel owner) throws SQLException {
		Database.getDatabase().update("UPDATE file SET owner=? WHERE id=?", owner.getId(), this.id);
	}
	
	public Date getUploadTime() throws SQLException {
		Timestamp upload = (Timestamp) Database.getDatabase().selectSingleColumn("SELECT upload_time FROM file WHERE id=?", this.id);
		return new Date(upload.getTime());
	}
	public void setUploadTime(Date uploadTime) throws SQLException {
		Database.getDatabase().update("UPDATE file SET upload_time=? WHERE id=?", new Timestamp(uploadTime.getTime()), this.id);
	}
	
	public void getContent(ContentReader reader) throws IOException, SQLException {
		Database db = Database.getDatabase();
		try(Connection conn = db.getConnection();
		    PreparedStatement stmt = conn.prepareStatement("SELECT content FROM file WHERE id=?")) {
			stmt.setInt(1, this.id);
			try(ResultSet rs = stmt.executeQuery()) {
				if(rs.next()) {
					reader.onFileBlobReady(rs.getBlob(1));
				} else {
					reader.onFileDoesNotExist();
				}
			}
		}
	}
	
	public void setContent(byte[] contents) throws SQLException {
		Database db = Database.getDatabase();
		try(Connection conn = db.getConnection()) {
			Blob b = conn.createBlob();
			b.setBytes(1, contents);
			try(PreparedStatement stmt = conn.prepareStatement("UPDATE file SET content=? WHERE id=?")) {
				stmt.setBlob(1, b);
				stmt.setInt(2, this.id);
			}
		}
	}
	
	public void setContent(InputStream stream) throws SQLException {
		Database db = Database.getDatabase();
		try(Connection conn = db.getConnection();
		    PreparedStatement stmt = conn.prepareStatement("UPDATE file SET content=? WHERE id=?")) {
			stmt.setBlob(1, stream);
			stmt.setInt(2, this.id);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this) return true;
		if(obj == null) return false;
		if(obj instanceof FileModel) {
			FileModel f = (FileModel) obj;
			return f.id == this.id;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return this.id;
	}
	
	public interface ContentReader {
		public void onFileBlobReady(Blob blob) throws IOException, SQLException;
		public void onFileDoesNotExist() throws IOException;
	}
}

