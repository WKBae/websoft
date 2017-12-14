package com.dbdbdeep.websoft.models;

public class FileModel {
    public static FileModel get(int id){
        return new FileModel(id);
    }

    public static FileModel getFile(int parent, String file_name)

    private final int id;

    private FileModel(int id){
        this.id = id;
    }
}
