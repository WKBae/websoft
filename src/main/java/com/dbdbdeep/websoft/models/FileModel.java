package com.dbdbdeep.websoft.models;

import java.sql.*;
import java.util.Date;
import java.lang.String;


public class FileModel {

    public static FileModel get(int id) throws SQLException {
        Database db = Database.getDatabase();
        Object idColumn = db.selectSingleColumn("SELECT id FROM file WHERE id=?", id);
        if(idColumn == null) return null;
        else return new FileModel(id);
    }

    public static FileModel getFile(int parent, String fileName) throws SQLException  {
        Database db = Database.getDatabase();
        Object idColumn = db.selectColumns("SELECT parent, file_name FROM file WHERE parent=? AND file_name=?", parent, fileName);
        if(idColumn == null) return null;
        else return new FileModel((Integer) idColumn);
    }

    public static FileModel create(int parent, String fileName, int owner, Date uploadTime, byte[] contents) throws SQLException {
        Database db = Database.getDatabase();
        try(Connection conn = db.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO user (parent, fileName, owner, uploadTime, contents) VALUES (?, ?, ?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            stmt.setInt(1, parent);
            stmt.setString(2, fileName);
            stmt.setInt(3, owner);
            stmt.setDate(4, new java.sql.Date(uploadTime.getTime()));
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


    public int getId(){
        return id;
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
        return (Date) Database.getDatabase().selectSingleColumn("SELECT upload_time FROM file WHERE id=?", this.id);
    }

    public void setUploadTime(Date uploadTime) throws SQLException {
        Database.getDatabase().update("UPDATE file SET upload_time=? WHERE id=?", uploadTime, this.id);
    }

    public byte[] getContents() throws SQLException {
        return (byte[]) Database.getDatabase().selectSingleColumn("SELECT contents FROM file WHERE id=?", this.id);
    }

    public void setContents(byte[] contents) throws SQLException {
        Database.getDatabase().update("UPDATE file SET contents=? WHERE id=?", contents, this.id);
    }

}

