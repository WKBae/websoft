package com.dbdbdeep.websoft.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class FolderPermissionModel {
    public static void createTable() throws SQLException {
        try(Connection conn = Database.getDatabase().getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS folder_permission (" +
                            "folder_id INT NOT NULL," +
                            "user_id INT NOT NULL," +
                            "readable TINYINT(1) DEFAULT 0 NOT NULL," +
                            "writable TINYINT(1) DEFAULT 0 NOT NULL," +
                            "permittable TINYINT(1) DEFAULT 0 NOT NULL," +
                            "PRIMARY KEY (folder_id, user_id)," +
                            "FOREIGN KEY (folder_id) REFERENCES folder(id) ON DELETE CASCADE)," +
                            "FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE)," +
                    ");"
            );
            stmt.execute();
        }
    }

    public static FolderPermissionModel get(FolderModel folderModel, UserModel userModel) throws SQLException{
        Database db = Database.getDatabase();
        Object[] idColumn = db.selectColumns("SELECT folder_id, user_id FROM folder_permission WHERE folder_id = ? AND user_id = ?", folderModel.getId(), userModel.getId());
        if(idColumn == null) return null;
        else return new FolderPermissionModel(folderModel.getId(), userModel.getId());
    }

    public static FolderPermissionModel create(FolderModel folderModel, UserModel userModel, boolean readable, boolean writable, boolean permittable) throws SQLException {
        Database db = Database.getDatabase();
        try(Connection conn = db.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO folder_permission (folder_id, user_id, readable, writable, permittable) VALUES (?, ?, ?, ?, ?)"
            );
            stmt.setInt(1, folderModel.getId());
            stmt.setInt(2, userModel.getId());
            stmt.setBoolean(3, readable);
            stmt.setBoolean(4, writable);
            stmt.setBoolean(5, permittable);

            int updatedRows = stmt.executeUpdate();
            if(updatedRows < 1) return null;
            else return FolderPermissionModel(folderModel.getId(), userModel.getId());
        }
    }

    private final int folderId, userId;

    private FolderPermissionModel(int folderId, int userId) throws SQLException {
        this.folderId = folderId;
        this.userId = userId;
    }

    public FolderModel getFolder() throws SQLException {
        return FolderModel.get(folderId);
    }

    public UserModel getUser(int id) throws SQLException {
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
}
