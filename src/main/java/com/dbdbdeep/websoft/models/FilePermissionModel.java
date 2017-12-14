package com.dbdbdeep.websoft.models;

public class FilePermissionModel {
    public static FilePermissionModel get(FileModel fileModel, UserModel userModel) {
        // select .. from file where (FileModel.getId=fileId) and (UserModel.getId=userId)

        return new FilePermissionModel(fileModel, userModel);
    }

    public static FilePermissionModel create(FileModel fileModel, UserModel userModel, boolean readable, boolean permittable) {
        // insert into file_permission values (...)xzc
        // return new FilePermission(fileModel, userModel);
    }

    private final int fileId, userId;

    public int getFileId(){

    }

    public int getUserId(){

    }

    public boolean isReadable() {
        // isReadable = select is_readable from file_permission where id=id
        // return isReadable;
    }

    public void setReadable(boolean readable) {
        // update file_permission set is_readable=? where id=id
    }

    public boolean isPermittable() {
        // isPermittable = select is_permittable from file_permission where id=id
        // return isPermittalbe;
    }

    public void setPermittable(boolean permittable) {
        // update user set is_permittable=? where id=id
    }

}
