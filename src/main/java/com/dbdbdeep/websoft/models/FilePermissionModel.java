package com.dbdbdeep.websoft.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class FilePermissionModel{
    public static void createTable() throws SQLException {
        try(Connection conn = Database.getDatabase().getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS file_permission (" +
                            "file_id int not null," +
                            "user_id int not null," +
                            "readable tinyint(1) not null default 0," +
                            "permittable tinyint(1) not null default 0," +
                            "primary key(file_id, user_id)," +
                            "foreign key(file_id) references file(id) on delete cascade," +
                            "foreign key(user_id) references user(id) on delete cascade" +
                            ");"
            );
            stmt.execute();
        }
    }

    public static FilePermissionModel get(FileModel fileModel, UserModel userModel) throws SQLException{
        Database db = Database.getDatabase();
        Object idColumn = db.selectColumns("SELECT file_id, user_id FROM file_permission WHERE file_id=? AND user_id=?", fileModel.getId(), userModel.getId());
        if(idColumn == null) return null;

        else return new FilePermissionModel(fileModel.getId(), userModel.getId());
    }

    public static FilePermissionModel[] findPermissions(UserModel user) throws SQLException{
        Database db = Database.getDatabase();
        try(Connection conn = db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT file_id FROM file_permission WHERE user_id = ?")) {
            stmt.setInt(1, user.getId());
            try(ResultSet rs = stmt.executeQuery()) {
                return readFilePermissionIds(rs, user.getId());
            }
        }
    }

    private static FilePermissionModel[] readFilePermissionIds(ResultSet rs, int userId) throws SQLException {
        LinkedList<FilePermissionModel> models = new LinkedList<>();
        while(rs.next()) {
            models.add(FilePermissionModel.getUnchecked(rs.getInt(1), userId));
        }
        return models.toArray(new FilePermissionModel[0]);
    }

    static FilePermissionModel getUnchecked(int fileId, int userId) throws SQLException{
        return new FilePermissionModel(fileId, userId);
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
            else return new FilePermissionModel(fileModel.getId(), userModel.getId());
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
