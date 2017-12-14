package com.dbdbdeep.websoft.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FilePermissionModel{
    public static FilePermissionModel get(FileModel fileModel, UserModel userModel) throws SQLException{
        Database db = Database.getDatabase();
        Object idColumn = db.selectColumn("SELECT file_id, user_id FROM file_permission WHERE file_id=? AND user_id=?", fileModel.getId(), userModel.getId());
        if(idColumn == null) return null;

        else return new FilePermissionModel(fileModel.getId(), userModel.getId());
    }

    public static FilePermissionModel create(FileModel fileModel, UserModel userModel, boolean readable, boolean permittable) throws SQLException{
        Database db = Database.getDatabase();
        try(Connection conn = db.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO file_permission (file_id, user_id, readable, permittable) VALUES (?, ?, ?, ?, ?)");
            stmt.setInt(1, fileModel.getId());
            stmt.setInt(2, userModel.getId());
            stmt.setBoolean(3, readable);
            stmt.setBoolean(4, permittable);

            int updatedRows = stmt.executeUpdate();
            if(updatedRows < 1) return null;
            else return FilePermissionModel(fileModel.getId(), userModel.getId());
        }
    }

    private final int fileId, userId;

    private FilePermissionModel(int fileId, int userId){
        this.fileId = fileId;
        this.userId = userId;
    }

    public FileModel getFile() throws SQLException{
        return FileModel.get(fileId);
    }

    public UserModel getUser() throws SQLException{
        return UserModel.get(userId);
    }

    public boolean isReadable() throws SQLException{
        return Boolean.TRUE.equals(Database.getDatabase().selectSingleColumn("SELECT readable FROM file_permission WHERE file_id=? AND user_id=?", this.fileId, this.userId));
    }

    public void setReadable(boolean readable) throws SQLException{
        Database.getDatabase().update("UPDATE readable SET readable=? WHERE file_id=? AND user_id=?", readable, this.fileId, this.userId);
    }

    public boolean isPermittable() throws SQLException{
        return Boolean.TRUE.equals(Database.getDatabase().selectSingleColumn("SELECT permittable FROM file_permission WHERE file_id=? AND user_id=?", this.fileId, this.userId));
    }

    public void setPermittable(boolean permittable) throws SQLException{
        Database.getDatabase().update("UPDATE readable SET readable=? WHERE file_id=? AND user_id=?", permittable, this.fileId, this.userId);
    }

}
