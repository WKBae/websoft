package com.dbdbdeep.websoft.models;

import java.sql.Blob;
import java.util.Date;

public class FileModel {

    public static FileModel get(int id) {
        return new FileModel(id);
    }

    public static FileModel getFile(int parent, String file_name) {
        //...
    }

    public static FileModel create(int parent, String fileName, int owner, Date uploadTime, byte[] contents)

    private final int id;

    private FileModel(int id) {
        this.id = id;
    }

    public String getFileName(){
        //...
    }

    public void setFileName(String fileName){
        //...
    }

    public int getOwner(){
        //...
    }

    public void setOwner(int owner){
        //...
    }

    public Date getUploadTime(){
        //...
    }

    public void setUploadTime(Date uploadTime){
        //...
    }

    public byte[] getContents(){
        //...
    }

    public void setContents(byte[] contents){
        //...
    }

}

