package com.dbdbdeep.websoft.models;

import java.sql.Blob;
import java.util.Date;

public class FileModel {

    public static FileModel get(int id) {
        // select .. from file where id=id
        // return new FileModel / return null
        return new FileModel(id);
    }

    public static FileModel getFile(int parent, String fileName) {
        // select .. from file where fileName = fileName and parent = parent
        // return new FileModel(id);
    }

    public static FileModel create(int parent, String fileName, int owner, Date uploadTime, byte[] contents){
        //...
    }

    private final int id;

    private FileModel(int id) {
        this.id = id;
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

