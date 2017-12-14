package com.dbdbdeep.websoft.models;

public class FilePermissionModel {
    public static FileModel get(int id) {
        // select .. from user where id=id
        // return new UserModel / return null
        return new UserModel(id);
    }

    public static UserModel getUser(String username) {
        // select .. from user where username=username
        // return new UserModel(id);
    }

    public static UserModel create(String username, String password, String name, String email, boolean isAdmin) {
        // insert into user values (...)
        // return new UserModel(id);
    }


    public int getFileId(){

    }
    public void setFileId(int fileId){
        // update user set name=? where id=id
    }
    public int getUserId(){

    }
    public void setUserId(int userId){

    }
    public boolean isReadable() {
        // isAdmin = select is_admin from user where id=id
        // return isAdmin;
    }

    public void setReadable(boolean readable) {
        // update user set is_admin=? where id=id
    }

    public boolean isPermittable() {
        // isAdmin = select is_admin from user where id=id
        // return isAdmin;
    }

    public void setPermittable(boolean permittable) {
        // update user set is_admin=? where id=id
    }

}
