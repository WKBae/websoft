<%@ tag description="File list template" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="util" uri="http://websoft.dbdbdeep.com/tags/util" %>

<%@ attribute name="path" required="true" %>
<%@ attribute name="folders" required="true" type="com.dbdbdeep.websoft.models.FolderModel[]" %>
<%@ attribute name="files" required="true" type="com.dbdbdeep.websoft.models.FileModel[]" %>

<%@ attribute name="searchBase" %>
<%@ attribute name="folderBase" %>
<%@ attribute name="downloadBase" %>
<%@ attribute name="uploadBase" %>

<t:bootstrap title="WebSoft :: ${path}">
    <jsp:attribute name="head">
        <style>
            #show-types .btn.active {
                box-shadow: none;
            }

            .upload-file-item:first-of-type .upload-file-remove {
                visibility: hidden;
            }

            .upload-file-loading {
                position: absolute;
                top: 0;
                bottom: 0;
                left: 0;
                right: 0;
            }
        </style>
    </jsp:attribute>
    <jsp:attribute name="script">
        <script>
            var currentPath = "${util:escapeJS(path)}";
        </script>
        <script src="<c:url value="/static/js/filelist.js"/>"></script>
    </jsp:attribute>
    <jsp:body>
        <nav class="navbar navbar-expand-lg navbar-light bg-light">
            <a class="navbar-brand" href="<c:url value="/"/>"><i class="far fa-archive"></i> WebSoft</a>
            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent"
                    aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarSupportedContent">
                <ul class="navbar-nav mr-auto">
                    <li class="nav-item active">
                        <a class="nav-link" href="<c:url value="/files/"/>">파일</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#">공유받은 파일</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#">공유한 파일</a>
                    </li>
                </ul>
                <form method="GET" action="${searchBase}${path}" class="form-inline my-2 my-lg-0">
                    <div class="input-group">
                        <input class="form-control" type="search" name="keyword" placeholder="파일 검색">
                        <div class="input-group-btn">
                            <button class="btn btn-info" type="submit"><i class="far fa-search"></i></button>
                        </div>
                    </div>
                </form>
            </div>
        </nav>

        <nav aria-label="folder path" role="navigation">
            <ol class="breadcrumb px-5">
                <c:set var="linkPath"><c:url value="${folderBase}/"/></c:set>
                <c:choose>
                    <c:when test="${path != '/'}">
                        <li class="breadcrumb-item"><a href="<c:url value="${linkPath}"/>"><i
                                class="fas fa-folder"></i></a></li>
                    </c:when>
                    <c:otherwise>
                        <li class="breadcrumb-item active"><i class="fas fa-folder"></i></li>
                    </c:otherwise>
                </c:choose>

                <c:forTokens var="entry" varStatus="status" items="${path}" delims="/">
                    <c:set var="linkPath" value="${linkPath}${entry}/"/>
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
                            <input type="radio" name="show-type" id="show-list" autocomplete="off" checked>
                            <i class="far fa-list"></i>
                        </label>
                        <label class="btn btn-outline-dark">
                            <input type="radio" name="show-type" id="show-icons" autocomplete="off">
                            <i class="fas fa-th"></i>
                        </label>
                    </div>

                    <div class="list-group">
                        <a href="#" class="list-group-item list-group-item-action" data-toggle="modal"
                           data-target="#folder-modal" id="folder-btn">
                            <i class="far fa-plus"></i> 폴더 생성
                        </a>
                        <a href="#" class="list-group-item list-group-item-action" data-toggle="modal"
                           data-target="#upload-modal" id="upload-btn">
                            <i class="far fa-upload"></i> 업로드
                        </a>
                        <a href="#" class="list-group-item list-group-item-action" id="delete-btn">
                            <i class="far fa-trash"></i> 삭제
                        </a>
                    </div>
                </div>

                <div class="col-12 col-md-9 order-md-first">
                    <c:forEach var="folder" items="${folders}">
                        <div class="folder-entry" data-path="${util:joinPath("", path, folder.name)}">
                            <div class="form-check form-check-inline">
                                <label class="form-check-label">
                                    <input type="checkbox" class="form-check-input position-static folder-check">
                                </label>
                            </div>
                            <a href="<c:url value="${util:joinPath(folderBase, path, folder.name)}"/>">

                                <i class="far fa-folder-open"></i> ${folder.name}
                            </a>
                        </div>
                    </c:forEach>
                    <c:forEach var="file" items="${files}">
                        <div class="file-entry" data-path="${util:joinPath("", path, file.name)}">
                            <div class="form-check form-check-inline">
                                <label class="form-check-label">
                                    <input type="checkbox" class="form-check-input position-static file-check">
                                </label>
                            </div>
                            <a href="<c:url value="${util:joinPath(downloadBase, path, file.name)}"/>">
                                <i class="far fa-file"></i> ${file.name}
                            </a>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>

        <div class="modal fade" id="folder-modal" tabindex="-1" role="dialog" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <form id="folder-form">
                        <div class="modal-header">
                            <h5 class="modal-title" id="folder-modal-title">폴더 생성...</h5>
                            <button type="button" class="close" data-dismiss="modal">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">
                            <div class="form-group">
                                <label for="folder-name-input">폴더 이름</label>
                                <input type="text" class="form-control" id="folder-name-input" placeholder="생성할 폴더의 이름"
                                       required>
                                <div class="invalid-feedback">
                                    폴더명을 입력해주세요.
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" id="folder-cancel" data-dismiss="modal">
                                취소
                            </button>
                            <button type="submit" class="btn btn-primary" id="folder-create">
                                확인
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <div class="modal fade" id="upload-modal" tabindex="-1" role="dialog" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <form id="upload-file-form">
                        <div class="modal-header">
                            <h5 class="modal-title" id="upload-modal-title">파일 업로드</h5>
                            <button type="button" class="close" data-dismiss="modal">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">
                            <div class="container-fluid px-0">
                                <div class="row" id="upload-file-list">
                                    <div class="col-12 pb-1 upload-file-item">
                                        <div class="upload-file-input">
                                            <button class="btn btn-link btn-sm px-1 text-danger upload-file-remove">
                                                <i class="far fa-minus"></i></button>
                                            <label class="custom-file ml-2">
                                                <input type="file" class="custom-file-input file-upload" required>
                                                <span class="custom-file-control"></span>
                                            </label>
                                        </div>
                                        <div class="upload-file-progress d-none">
                                            <span class="filename"></span>
                                            <div class="progress">
                                                <div class="progress-bar progress-bar-striped progress-bar-animated"
                                                     role="progressbar" style="width:0"></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <button type="button" class="btn btn-link btn-sm" id="upload-file-add">
                                <i class="far fa-plus"></i> 추가하기...
                            </button>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" id="upload-file-cancel"
                                    data-dismiss="modal">취소
                            </button>
                            <div class="position-relative">
                                <button type="submit" class="btn btn-primary" id="upload-files"><i
                                        class="fas fa-upload"></i> 업로드
                                </button>
                                <div class="upload-file-loading d-none">
                                    <div class="d-table w-100 h-100">
                                        <div class="d-table-cell text-center align-middle">
                                            <i class="fas fa-spinner fa-pulse"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </jsp:body>
</t:bootstrap>
