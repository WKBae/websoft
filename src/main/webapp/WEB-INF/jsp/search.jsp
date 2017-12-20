<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="util" uri="http://websoft.dbdbdeep.com/tags/util" %>

<t:filebase
        path="${path}"
        files="${files}"
        folders="${folders}"

        searchBase="/search"
        folderBase="/files"
        fileBase="/file"

        canCreate="false"
        canDelete="false"
/>
