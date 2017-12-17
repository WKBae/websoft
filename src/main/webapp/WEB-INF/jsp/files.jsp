<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="folderBasePath" value="/files${path != '/'? path : ''}"/>
<c:set var="fileBasePath" value="/download${path != '/'? path : ''}"/>
<c:set var="uploadPath" value="/upload${path != '/'? path : ''}"/>

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
            var $types = $("#show-types");
            var $typeBtns = $types.find(".btn");

            function applyChecked() {
                var $checked = $typeBtns.filter(function () {
                    return $(this).find("input")[0].checked;
                });
                var $unchecked = $typeBtns.not($checked);
                $checked.removeClass("btn-outline-dark").addClass("btn-dark");
                $unchecked.removeClass("btn-dark").addClass("btn-outline-dark");
            }

            $typeBtns.on("change", applyChecked);
            applyChecked.apply($typeBtns[0]);


            var $uploadFiles = $("#upload-file-list");
            $uploadFiles.on('change', "input[type=file].file-upload", function () {
                $(this).next('.custom-file-control').text(this.files[0].name);
            }).on('click', ".upload-file-item:not(:first-of-type) .upload-file-remove", function () {
                $(this).closest(".upload-file-item").remove();
            });
            $("#upload-file-add").on('click', function () {
                var $cloned = $(".upload-file-item").eq(0).clone();
                $cloned.find("input[type=file]").val("");
                $cloned.find(".custom-file-control").text("");
                $uploadFiles.append($cloned);
            });
            $("#upload-file-form").on('submit', function (e) {
                var $files = $uploadFiles.find("input[type=file]");
                $files.attr("disabled", true);
                $("#upload-files").attr("disabled", true).next(".upload-file-loading").removeClass("d-none");

                var filesToUpload = $files.map(function () {
                    return this.files[0];
                }).get();
                var $progresses = $files.closest(".upload-file-item").find(".upload-file-progress .progress-bar");

                postUpload(filesToUpload, $progresses.get(), function() {
                    alert("completed"); // TODO
                });

                function fileProgress(progress) {
                    return function(e) {
                        if (e.lengthComputable) {
                            var max = e.total;
                            var current = e.loaded;
                            var percent = (current * 100) / max;
                            if (percent >= 100) {
                                percent = 100;
                            }
                            progress.style.width = percent + "%";
                        }
                    }
                }
                function postUpload(files, progresses, callback) {
                    if(files.length <= 0) {
                        return callback();
                    }
                    var file = files.pop();
                    var progress = progresses.pop();

                    var formData = new FormData();
                    formData.append("file", file);

                    $.ajax({
                        type: 'POST',
                        url: '<c:url value="${uploadPath}"/>',
                        data: formData,
                        cache: false,
                        contentType: false,
                        processData: false,
                        beforeSend: function(xhr) {
                            if (xhr.upload) {
                                xhr.upload.addEventListener('progress', fileProgress(progress), false);
                            }
                        },
                        success: function() {
                            $(progress).addClass("bg-success");
                            postUpload(files, progresses, callback);
                        },
                        error: function() {
                            $(progress).addClass("bg-danger");
                            postUpload(files, progresses, callback);
                        }
                    });
                }

                return false;
            });

            $("#upload-modal").on('hidden.bs.modal', function () {
                var $items = $(this).find(".upload-file-item");
                $items.filter(":not(:first-of-type)").remove()
                $items.find("input").val("");
                $items.find("custom-form-control").text("");
                $(".upload-file-loading").addClass("d-none");
            });
        </script>
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
                <c:set var="linkPath"><c:url value="/files/"/></c:set>
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
                    <br>
                    <button class="btn btn-outline-primary" id="upload-btn" data-toggle="modal"
                            data-target="#upload-modal">
                        <i class="far fa-upload"></i> 업로드
                    </button>
                </div>
                <div class="col-12 col-md-9 order-md-first">
                    <c:forEach var="folder" items="${folders}">
                        <a href="<c:url value="${folderBasePath}/${folder.name}"/>">
                            <i class="far fa-folder-open"></i> ${folder.name}
                        </a>
                        <br>
                    </c:forEach>
                    <c:forEach var="file" items="${files}">
                        <a href="<c:url value="${fileBasePath}/${file.fileName}"/>">
                            <i class="far fa-file"></i> ${file.fileName}
                        </a>
                        <br>
                    </c:forEach>
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
                                                     role="progressbar" style="width: 0%"></div>
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
