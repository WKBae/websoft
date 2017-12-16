<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:bootstrap title="WebSoft :: ${path}">
    <jsp:attribute name="head">
        <style>
            #show-types .btn.active {
                box-shadow: none;
            }
        </style>
    </jsp:attribute>
    <jsp:attribute name="script">
        <script>
            var $types = $("#show-types");
            var $typeBtns = $types.find(".btn");
            function applyChecked() {
                var $checked = $typeBtns.filter(function() {
                    return $(this).find("input")[0].checked;
                });
                var $unchecked = $typeBtns.not($checked);
                $checked.removeClass("btn-outline-dark").addClass("btn-dark");
                $unchecked.removeClass("btn-dark").addClass("btn-outline-dark");
            }
            $typeBtns.on("change", applyChecked);
            applyChecked.apply($typeBtns[0]);
        </script>
    </jsp:attribute>
    <jsp:body>
        <nav class="navbar navbar-expand-lg navbar-light bg-light">
            <a class="navbar-brand" href="<c:url value="/"/>"><i class="far fa-archive"></i> WebSoft</a>
            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarSupportedContent">
                <ul class="navbar-nav mr-auto">
                    <li class="nav-item active">
                        <a class="nav-link" href="<c:url value="/files"/>">파일</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#">공유받은 파일</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#">공유한 파일</a>
                    </li>
                </ul>
                <form class="form-inline my-2 my-lg-0">
                    <div class="input-group">
                        <input class="form-control" type="search" placeholder="파일 검색">
                        <div class="input-group-btn">
                            <button class="btn btn-info" type="submit"><i class="far fa-search"></i></button>
                        </div>
                    </div>
                </form>
            </div>
        </nav>

        <nav aria-label="folder path" role="navigation">
            <ol class="breadcrumb px-5">
                <c:set var="linkPath"><c:url value="/files"/></c:set>
                <c:choose>
                    <c:when test="${path != '/'}">
                        <li class="breadcrumb-item"><a href="<c:url value="${linkPath}/"/>"><i class="fas fa-folder"></i></a></li>
                    </c:when>
                    <c:otherwise>
                        <li class="breadcrumb-item active"><i class="fas fa-folder"></i></li>
                    </c:otherwise>
                </c:choose>

                <c:forTokens var="entry" varStatus="status" items="${path}" delims="/">
                    <c:set var="linkPath" value="${linkPath}/${entry}"/>
                    <c:choose>
                        <c:when test="${not status.last}">
                            <li class="breadcrumb-item"><a href="<c:url value="${linkPath}"/>">${entry}</a></li>
                        </c:when>
                        <c:otherwise>
                            <li class="breadcrumb-item active">${entry}</li>
                        </c:otherwise>
                    </c:choose>
                </c:forTokens>
            </ol>
        </nav>

        <div class="container">
            <div class="row">
                <div class="col">
                    <div class="btn-group" data-toggle="buttons" id="show-types">
                        <label class="btn btn-dark active">
                            <input type="radio" name="show-type" id="show-list" autocomplete="off" checked><i class="far fa-list"></i>
                        </label>
                        <label class="btn btn-outline-dark">
                            <input type="radio" name="show-type" id="show-icons" autocomplete="off"><i class="fas fa-th"></i>
                        </label>
                    </div>
                    <br>
                    메뉴
                </div>
                <div class="col-12 col-md-9 order-md-first">
                    파일 목록
                </div>
            </div>
        </div>
    </jsp:body>
</t:bootstrap>
