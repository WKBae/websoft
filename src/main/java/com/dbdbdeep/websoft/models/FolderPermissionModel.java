package com.dbdbdeep.websoft.models;

import com.dbdbdeep.websoft.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class FolderPermissionModel {
	public static void createTable() throws SQLException {
		try (Connection conn = Database.getDatabase().getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS folder_permission (" +
							"folder_id INT NOT NULL," +
							"user_id INT NOT NULL," +
							"readable TINYINT(1) DEFAULT 0 NOT NULL," +
							"writable TINYINT(1) DEFAULT 0 NOT NULL," +
							"permittable TINYINT(1) DEFAULT 0 NOT NULL," +
							"PRIMARY KEY (folder_id, user_id)," +
							"FOREIGN KEY (folder_id) REFERENCES folder(id) ON DELETE CASCADE," +
							"FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE" +
							");"
			);
			stmt.execute();
		}
	}

	public static FolderPermissionModel get(FolderModel folderModel, UserModel userModel) throws SQLException {
		Database db = Database.getDatabase();
		Object[] idColumn = db.selectColumns("SELECT folder_id, user_id FROM folder_permission WHERE folder_id = ? AND user_id = ?", folderModel.getId(), userModel.getId());
		if (idColumn == null) return null;
		else return new FolderPermissionModel(folderModel.getId(), userModel.getId());
	}

	public static FolderPermissionModel[] findPermissions(UserModel user) throws SQLException {
		Database db = Database.getDatabase();
		try (Connection conn = db.getConnection();
		     PreparedStatement stmt = conn.prepareStatement("SELECT folder_id FROM folder_permission WHERE user_id = ?")) {
			stmt.setInt(1, user.getId());
			try (ResultSet rs = stmt.executeQuery()) {
				return readFolderPermissionIdsWithUser(rs, user.getId());
			}
		}
	}

	public static FolderPermissionModel[] findPermissions(FolderModel folder) throws SQLException {
		Database db = Database.getDatabase();
		try (Connection conn = db.getConnection();
		     PreparedStatement stmt = conn.prepareStatement("SELECT user_id FROM folder_permission WHERE folder_id = ?")) {
			stmt.setInt(1, folder.getId());
			try (ResultSet rs = stmt.executeQuery()) {
				return readFolderPermissionIdsWithFolder(rs, folder.getId());
			}
		}
	}

	private static FolderPermissionModel[] readFolderPermissionIdsWithUser(ResultSet rs, int userId) throws SQLException {
		LinkedList<FolderPermissionModel> models = new LinkedList<>();
		while (rs.next()) {
			models.add(FolderPermissionModel.getUnchecked(rs.getInt(1), userId));
		}
		return models.toArray(new FolderPermissionModel[0]);
	}

	private static FolderPermissionModel[] readFolderPermissionIdsWithFolder(ResultSet rs, int folderId) throws SQLException {
		LinkedList<FolderPermissionModel> models = new LinkedList<>();
		while (rs.next()) {
			models.add(FolderPermissionModel.getUnchecked(folderId, rs.getInt(1)));
		}
		return models.toArray(new FolderPermissionModel[0]);
	}

	static FolderPermissionModel getUnchecked(int folderId, int userId) throws SQLException {
		return new FolderPermissionModel(folderId, userId);
	}

	public static FolderPermissionModel create(FolderModel folderModel, UserModel userModel, boolean readable, boolean writable, boolean permittable) throws SQLException {
		Database db = Database.getDatabase();
		try (Connection conn = db.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO folder_permission (folder_id, user_id, readable, writable, permittable) VALUES (?, ?, ?, ?, ?)"
			);
			stmt.setInt(1, folderModel.getId());
			stmt.setInt(2, userModel.getId());
			stmt.setBoolean(3, readable);
			stmt.setBoolean(4, writable);
			stmt.setBoolean(5, permittable);

			int updatedRows = stmt.executeUpdate();
			if (updatedRows < 1) return null;
			else return new FolderPermissionModel(folderModel.getId(), userModel.getId());
		}
	}

	public void delete() throws SQLException {
		Database.getDatabase().update("DELETE FROM folder_permission WHERE folder_id=? AND user_id=?", this.folderId, this.userId);
	}

	private final int folderId, userId;

	private FolderPermissionModel(int folderId, int userId) throws SQLException {
		this.folderId = folderId;
		this.userId = userId;
	}

	public FolderModel getFolder() throws SQLException {
		return FolderModel.get(folderId);
	}

	public UserModel getUser() throws SQLException {
		return UserModel.get(userId);
	}

	public boolean isReadable() throws SQLException {
		return Boolean.TRUE.equals(Database.getDatabase().selectSingleColumn("SELECT readable FROM folder_permission WHERE folder_id = ? AND user_id = ?", this.folderId, this.userId));
	}

	public void setReadable(boolean isReadable) throws SQLException {
		Database.getDatabase().update("UPDATE folder_permission SET readable = ? WHERE folder_id = ? AND user_id = ?", isReadable, this.folderId, this.userId);
	}

	public boolean isWritable() throws SQLException {
		return Boolean.TRUE.equals(Database.getDatabase().selectSingleColumn("SELECT writable FROM folder_permission WHERE folder_id = ? AND user_id = ?", this.folderId, this.userId));
	}

	public void setWritable(boolean isWritable) throws SQLException {
		Database.getDatabase().update("UPDATE folder_permission SET writable = ? WHERE folder_id = ? AND user_id = ?", isWritable, this.folderId, this.userId);
	}

	public boolean isPermittable() throws SQLException {
		return Boolean.TRUE.equals(Database.getDatabase().selectSingleColumn("SELECT permittable FROM folder_permission WHERE folder_id = ? AND user_id = ?", this.folderId, this.userId));
	}

	public void setPermittable(boolean isPermittable) throws SQLException {
		Database.getDatabase().update("UPDATE folder_permission SET permittable = ? WHERE folder_id = ? AND user_id = ?", isPermittable, this.folderId, this.userId);
	}

	public FolderPermissionModel clone(FolderModel newTarget) throws SQLException {
		Database db = Database.getDatabase();
		db.update("INSERT INTO folder_permission (folder_id, user_id, readable, writable, permittable) SELECT ?, user_id, readable, writable, permittable FROM folder_permission WHERE folder_id=? AND user_id=?", newTarget.getId(), this.folderId, this.userId);
		return FolderPermissionModel.getUnchecked(newTarget.getId(), this.userId);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null) return false;
		if (obj instanceof FolderPermissionModel) {
			FolderPermissionModel f = (FolderPermissionModel) obj;
			return f.folderId == this.folderId && f.userId == this.userId;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.folderId * 31 + this.userId;
	}
}
