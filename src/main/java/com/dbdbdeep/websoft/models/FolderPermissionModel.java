package com.dbdbdeep.websoft.models;

public class FolderPermissionModel {
    public static FolderPermissionModel get(FolderModel folderModel, UserModel userModel){
        // select ... from folder_permission where folderModel.getId() = folderId and userModel.getId() = userId;
        return new FolderPermissionModel(folderModel, userModel);
    }

    public static FolderPermissionModel create(FolderModel folderModel, UserModel userModel, boolean readable, boolean writable, boolean permittable) {
        // insert into folder_permission values (...)
        // return new FolderPermissionModel(folderModel, userModel);
    }

    private final int folderId, userId;

    public int getFolderId() {

    }

    public int getUserId() {

    }

    public boolean isReadable() {
        // isReadable = select readable from folder_permission where folder_id = folderId and user_id = userId;
        // return isReadable;
    }

    public void setReadable(boolean isReadable) {
        // update folder_permission set readable = ? where folder_id = folderId and user_id = userId;
    }

    public boolean isWritable() {
        // isWritable = select writable from folder_permission where folder_id = folderId and user_id = userId;
        // return isWritable;
    }

    public void setWritable(boolean isWritable) {
        // update folder_permission set writable = ? where folder_id = folderId and user_id = userId;
    }

    public boolean isPermittable() {
        // isPermittable = select isPermittable from folder_permission where folder_id = folderId and user_id = userId;
        // return isPermittable;
    }

    public void setPermittable(boolean isPermittable) {
        // update folder_permission set permittable = ? where folder_id = folderId and user_id = userId;
    }
}
