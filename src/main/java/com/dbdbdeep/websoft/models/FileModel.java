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

    public static FileModel getFile(int parent, String fileName)  {
        //...
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

    public String getFileName(){
        // fileName = select fileName from file where filename=filename
        // return filename;
    }

    public void setFileName(String fileName){
        //...
    }

    public int getOwner(){
        // owner = select owner from file where id=id
        // return owner;
    }

    public void setOwner(int owner){
        //...
    }

    public Date getUploadTime(){
        // uploadTime = select uploadtime from file where id=id
        // return uploadTime;
    }

    public void setUploadTime(Date uploadTime){
        //...
    }

    public byte[] getContents(){
        // contents = select contents from file where id=id
        // return contents;
    }

    public void setContents(byte[] contents){
        //...
    }

}

