<%@ tag description="File list template" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="util" uri="http://websoft.dbdbdeep.com/tags/util" %>

<%@ attribute name="path" required="true" %>
<%@ attribute name="folders" required="true" type="java.util.Map<java.lang.String, com.dbdbdeep.websoft.models.FolderModel>" %>
<%@ attribute name="files" required="true" type="java.util.Map<java.lang.String, com.dbdbdeep.websoft.models.FileModel>" %>

<%@ attribute name="searchBase" %>
<%@ attribute name="listBase" %>
<%@ attribute name="fileBase" %>
<%@ attribute name="folderBase" %>

<%@ attribute name="canCreate" type="java.lang.Boolean" %>
<c:set var="canCreate" value="${empty canCreate? false : canCreate}"/>
<%@ attribute name="canModify" type="java.lang.Boolean" %>
<c:set var="canModify" value="${empty canModify? false : canModify}"/>
<%--TODO showPath--%>

<c:set var="requestPath" value="${requestScope['javax.servlet.forward.request_uri']}"/>

<t:bootstrap title="WebSoft :: ${path}">
    <jsp:attribute name="head">
        <style>
            #show-types .btn.active {
                box-shadow: none;
            }

            .upload-item:first-of-type .upload-remove {
                visibility: hidden;
            }

            .modal-loading {
                position: absolute;
                top: 0;
                bottom: 0;
                left: 0;
                right: 0;
            }

            #file-list .folder-entry .folder-check, #file-list .file-entry .file-check {
                visibility: hidden;
            }
            #file-list .folder-entry:hover .folder-check, #file-list .file-entry:hover .file-check {
                visibility: visible;
            }
            #file-list.checkbox-visible .folder-entry .folder-check, #file-list.checkbox-visible .file-entry .file-check {
                visibility: visible;
            }
        </style>
    </jsp:attribute>
    <jsp:attribute name="script">
        <script>
            var currentPath = "${util:escapeJS(path)}";
            var fileBase = "${util:escapeJS(fileBase)}";
            var folderBase = "${util:escapeJS(folderBase)}";
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
                    <li class="nav-item<c:if test="${requestPath.startsWith('/files')}"> active</c:if>">
                        <a class="nav-link" href="<c:url value="/files/"/>">파일</a>
                    </li>
                    <li class="nav-item<c:if test="${requestPath.startsWith('/shared')}"> active</c:if>">
                        <a class="nav-link" href="<c:url value="/shared/"/>">공유받은 파일</a>
                    </li>
                    <li class="nav-item<c:if test="${requestPath.startsWith('/sharing')}"> active</c:if>">
                        <a class="nav-link" href="<c:url value="/sharing/"/>">공유한 파일</a>
                    </li>
                </ul>
<c:if test="${not empty searchBase}">
                <form method="GET" action="${searchBase}${path}" class="form-inline my-2 my-lg-0">
                    <div class="input-group">
                        <input class="form-control" type="search" name="keyword" placeholder="파일 검색">
                        <div class="input-group-btn">
                            <button class="btn btn-info" type="submit"><i class="far fa-search"></i></button>
                        </div>
                    </div>
                </form>
</c:if>
            </div>
        </nav>
        <nav aria-label="folder path" role="navigation">
            <ol class="breadcrumb px-5">
                <c:set var="linkPath"><c:url value="${listBase}/"/></c:set>
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

                    <div class="list-group mb-3" id="normal-actions">
<c:if test="${canCreate}">
                        <a href="#" class="list-group-item list-group-item-action" data-toggle="modal"
                           data-target="#folder-modal" id="folder-btn">
                            <i class="far fa-plus"></i> 폴더 생성
                        </a>
</c:if>
<c:if test="${canCreate}">
                        <a href="#" class="list-group-item list-group-item-action" data-toggle="modal"
                           data-target="#upload-modal" id="upload-btn">
                            <i class="far fa-upload"></i> 업로드
                        </a>
</c:if>
                    </div>

                    <div class="list-group d-none" id="choice-actions">
<c:if test="${canModify}">
                        <a href="#" class="list-group-item list-group-item-action action action-single"
                           data-toggle="modal" data-target="#rename-modal" id="rename-btn">
                            <i class="far fa-edit"></i> 이름 바꾸기
                        </a>
</c:if>
<c:if test="${canModify}">
                        <a href="#" class="list-group-item list-group-item-action action action-single action-multiple"
                           id="copy-btn">
                            <i class="far fa-copy"></i> 복사
                        </a>
</c:if>
<c:if test="${canModify}">
                        <a href="#" class="list-group-item list-group-item-action action action-single action-multiple"
                           id="move-btn">
                            <i class="far fa-inbox-out"></i> 이동
                        </a>
</c:if>
<c:if test="${canModify}">
                        <a href="#" class="list-group-item list-group-item-action action action-single action-multiple"
                           id="permit-btn">
                            <i class="far fa-file-check"></i> 권한 설정
                        </a>
</c:if>
<c:if test="${canModify}">
                        <a href="#" class="list-group-item list-group-item-action list-group-item-danger action action-single action-multiple"
                           id="delete-btn">
                            <i class="far fa-trash"></i> 삭제
                        </a>
</c:if>
                    </div>
                </div>

                <div class="col-12 col-md-9 order-md-first" id="file-list">
                    <c:forEach var="folder" items="${folders}">
                        <div class="folder-entry" data-name="${folder.value.name}" data-path="${folder.key}">
                            <div class="form-check form-check-inline">
                                <label class="form-check-label">
                                    <input type="checkbox" class="form-check-input position-static folder-check">
                                </label>
                            </div>
                            <a href="<c:url value="${listBase}${folder.key}"/>">
                                <i class="far fa-folder-open"></i> ${folder.value.name}
                            </a>
                        </div>
                    </c:forEach>
                    <c:forEach var="file" items="${files}">
                        <div class="file-entry" data-name="${file.value.name}" data-path="${file.key}">
                            <div class="form-check form-check-inline">
                                <label class="form-check-label">
                                    <input type="checkbox" class="form-check-input position-static file-check">
                                </label>
                            </div>
                            <a href="<c:url value="${fileBase}${file.key}"/>">
                                <i class="far fa-file"></i> ${file.value.name}
                            </a>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>

<c:if test="${canCreate}">
        <t:modal id="folder-modal" formId="folder-form" title="폴더 생성...">
            <jsp:attribute name="body">
                <div class="form-group">
                    <label for="folder-name-input">폴더 이름</label>
                    <input type="text" class="form-control" id="folder-name-input" placeholder="생성할 폴더의 이름"
                           required>
                    <div class="invalid-feedback">
                        폴더명을 입력해주세요.
                    </div>
                </div>
            </jsp:attribute>
            <jsp:attribute name="footer">
                <button type="button" class="btn btn-secondary" id="folder-cancel" data-dismiss="modal">
                    취소
                </button>
                <button type="submit" class="btn btn-primary" id="folder-create">
                    확인
                </button>
            </jsp:attribute>
        </t:modal>
</c:if>

<c:if test="${canModify}">
    <t:modal id="rename-modal" formId="rename-form" title="이름 바꾸기">
    <jsp:attribute name="body">
        <div class="form-group">
            <label for="rename-input">새로운 이름</label>
            <input type="text" class="form-control" id="rename-input" placeholder="바꿀 이름"
                   required>
            <div class="invalid-feedback">
                이름을 입력해주세요.
            </div>
        </div>
    </jsp:attribute>
        <jsp:attribute name="footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">
            취소
        </button>
        <button type="submit" class="btn btn-primary">
            확인
        </button>
    </jsp:attribute>
    </t:modal>
</c:if>

<c:if test="${canCreate}">
        <t:modal id="upload-modal" formId="upload-form" title="파일 업로드">
            <jsp:attribute name="body">
                <div class="container-fluid px-0">
                    <div class="row" id="upload-list">
                        <div class="col-12 pb-1 upload-item">
                            <div class="upload-file-input">
                                <button class="btn btn-link btn-sm px-1 text-danger upload-remove">
                                    <i class="far fa-minus"></i></button>
                                <label class="custom-file ml-2">
                                    <input type="file" class="custom-file-input file-upload" required>
                                    <span class="custom-file-control"></span>
                                </label>
                            </div>
                            <div class="upload-progress d-none">
                                <span class="filename"></span>
                                <div class="progress">
                                    <div class="progress-bar progress-bar-striped progress-bar-animated"
                                         role="progressbar" style="width:0"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <button type="button" class="btn btn-link btn-sm" id="upload-add">
                    <i class="far fa-plus"></i> 추가하기...
                </button>
            </jsp:attribute>
            <jsp:attribute name="footer">
                <button type="button" class="btn btn-secondary" id="upload-cancel"
                        data-dismiss="modal">취소
                </button>
                <div class="position-relative">
                    <button type="submit" class="btn btn-primary" id="upload-files"><i
                            class="fas fa-upload"></i> 업로드
                    </button>
                    <div class="modal-loading d-none">
                        <div class="d-table w-100 h-100">
                            <div class="d-table-cell text-center align-middle">
                                <i class="fas fa-spinner fa-pulse"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </jsp:attribute>
        </t:modal>
</c:if>

<c:if test="${canModify}">
        <t:modal id="delete-modal" formId="delete-form" title="파일 삭제">
            <jsp:attribute name="body">
                아래 파일들을 삭제하시겠습니까?
                <ul class="list-unstyled" id="delete-list">

                </ul>
            </jsp:attribute>
            <jsp:attribute name="footer">
                <button type="button" class="btn btn-secondary" id="delete-cancel" data-dismiss="modal">
                    취소
                </button>
                <div class="position-relative">
                    <button type="submit" class="btn btn-danger" id="delete-confirm">
                        확인
                    </button>
                    <div class="modal-loading d-none">
                        <div class="d-table w-100 h-100">
                            <div class="d-table-cell text-center align-middle">
                                <i class="fas fa-spinner fa-pulse"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </jsp:attribute>
        </t:modal>
</c:if>

    </jsp:body>
</t:bootstrap>
