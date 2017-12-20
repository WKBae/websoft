<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:filebase
        path="${path}"
        files="${files}"
        folders="${folders}"

        searchBase="/shared/search"
        listBase="/shared/files"
        fileBase="/shared/file"
        folderBase="/shared/folder"
        permissionBase="/permission/shared"

        canCreate="false"
        canModify="false"
/>
