<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:filebase
        path="${path}"
        files="${files}"
        folders="${folders}"

        searchBase="/search"
        listBase="/files"
        fileBase="/file"
        folderBase="/folder"
        permissionBase="/permission"

        canCreate="true"
        canModify="true"
/>
