package com.dbdbdeep.websoft.models;

import com.dbdbdeep.websoft.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class FilePermissionModel {
	public static void createTable() throws SQLException {
		try (Connection conn = Database.getDatabase().getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS file_permission (" +
							"file_id INT NOT NULL," +
							"user_id INT NOT NULL," +
							"readable TINYINT(1) NOT NULL DEFAULT 0," +
							"permittable TINYINT(1) NOT NULL DEFAULT 0," +
							"PRIMARY KEY(file_id, user_id)," +
							"FOREIGN KEY(file_id) REFERENCES file(id) ON DELETE CASCADE," +
							"FOREIGN KEY(user_id) REFERENCES user(id) ON DELETE CASCADE" +
							");"
			);
			stmt.execute();
		}
	}

	public static FilePermissionModel get(FileModel fileModel, UserModel userModel) throws SQLException {
		Database db = Database.getDatabase();
		Object idColumn = db.selectColumns("SELECT file_id, user_id FROM file_permission WHERE file_id=? AND user_id=?", fileModel.getId(), userModel.getId());
		if (idColumn == null) return null;

		else return new FilePermissionModel(fileModel.getId(), userModel.getId());
	}

	public static FilePermissionModel[] findPermissions(UserModel user) throws SQLException {
		Database db = Database.getDatabase();
		try (Connection conn = db.getConnection();
		     PreparedStatement stmt = conn.prepareStatement("SELECT file_id FROM file_permission WHERE user_id = ?")) {
			stmt.setInt(1, user.getId());
			try (ResultSet rs = stmt.executeQuery()) {
				return readFilePermissionIdsWithUser(rs, user.getId());
			}
		}
	}

	public static FilePermissionModel[] findPermissions(FileModel file) throws SQLException {
		Database db = Database.getDatabase();
		try (Connection conn = db.getConnection();
		     PreparedStatement stmt = conn.prepareStatement("SELECT user_id FROM file_permission WHERE file_id = ?")) {
			stmt.setInt(1, file.getId());
			try (ResultSet rs = stmt.executeQuery()) {
				return readFilePermissionIdsWithFile(rs, file.getId());
			}
		}
	}

	private static FilePermissionModel[] readFilePermissionIdsWithUser(ResultSet rs, int userId) throws SQLException {
		LinkedList<FilePermissionModel> models = new LinkedList<>();
		while (rs.next()) {
			models.add(FilePermissionModel.getUnchecked(rs.getInt(1), userId));
		}
		return models.toArray(new FilePermissionModel[0]);
	}

	private static FilePermissionModel[] readFilePermissionIdsWithFile(ResultSet rs, int fileId) throws SQLException {
		LinkedList<FilePermissionModel> models = new LinkedList<>();
		while (rs.next()) {
			models.add(FilePermissionModel.getUnchecked(fileId, rs.getInt(1)));
		}
		return models.toArray(new FilePermissionModel[0]);
	}

	static FilePermissionModel getUnchecked(int fileId, int userId) throws SQLException {
		return new FilePermissionModel(fileId, userId);
	}

	public static FilePermissionModel create(FileModel fileModel, UserModel userModel, boolean readable, boolean permittable) throws SQLException {
		Database db = Database.getDatabase();
		try (Connection conn = db.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO file_permission (file_id, user_id, readable, permittable) VALUES (?, ?, ?, ?)");
			stmt.setInt(1, fileModel.getId());
			stmt.setInt(2, userModel.getId());
			stmt.setBoolean(3, readable);
			stmt.setBoolean(4, permittable);

			int updatedRows = stmt.executeUpdate();
			if (updatedRows < 1) return null;
			else return new FilePermissionModel(fileModel.getId(), userModel.getId());
		}
	}

	public void delete() throws SQLException {
		Database.getDatabase().update("DELETE FROM file_permission WHERE file_id=? AND user_id=?", this.fileId, this.userId);
	}

	private final int fileId, userId;

	private FilePermissionModel(int fileId, int userId) {
		this.fileId = fileId;
		this.userId = userId;
	}

	public FileModel getFile() throws SQLException {
		return FileModel.get(fileId);
	}

	public UserModel getUser() throws SQLException {
		return UserModel.get(userId);
	}

	public boolean isReadable() throws SQLException {
		return Boolean.TRUE.equals(Database.getDatabase().selectSingleColumn("SELECT readable FROM file_permission WHERE file_id=? AND user_id=?", this.fileId, this.userId));
	}

	public void setReadable(boolean readable) throws SQLException {
		Database.getDatabase().update("UPDATE readable SET readable=? WHERE file_id=? AND user_id=?", readable, this.fileId, this.userId);
	}

	public boolean isPermittable() throws SQLException {
		return Boolean.TRUE.equals(Database.getDatabase().selectSingleColumn("SELECT permittable FROM file_permission WHERE file_id=? AND user_id=?", this.fileId, this.userId));
	}

	public void setPermittable(boolean permittable) throws SQLException {
		Database.getDatabase().update("UPDATE readable SET readable=? WHERE file_id=? AND user_id=?", permittable, this.fileId, this.userId);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null) return false;
		if (obj instanceof FilePermissionModel) {
			FilePermissionModel f = (FilePermissionModel) obj;
			return f.fileId == this.fileId && f.userId == this.userId;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.fileId * 31 + this.userId;
	}
}
