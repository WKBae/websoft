<%@ tag description="Base page template using Bootstrap" pageEncoding="UTF-8" %>
<%@ attribute name="title" %>
<%@ attribute name="head" fragment="true" %>
<%@ attribute name="script" fragment="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <title>${title}</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link rel="stylesheet" href="<c:url value="/static/css/bootstrap.min.css"/>">
    <script defer src="<c:url value="/static/js/fontawesome-all.min.js"/>"></script>
    <style>
        .custom-file-control:empty::after {
            content: "파일 선택..."
        }
        .custom-file-control::before {
            content: "찾아보기"
        }
    </style>
    <jsp:invoke fragment="head"/>
</head>
<body>
    <jsp:doBody/>

    <script src="<c:url value="/static/js/jquery-3.2.1.min.js"/>"></script>
    <script src="<c:url value="/static/js/popper.min.js"/>"></script>
    <script src="<c:url value="/static/js/bootstrap.min.js"/>"></script>
    <jsp:invoke fragment="script"/>
</body>
</html>
