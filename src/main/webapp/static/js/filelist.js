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


function reloadPage() {
    document.location.reload();
}


$("#folder-form").on('submit', function () {
    this.disabled = true;
    $.ajax({
        type: "POST", // TODO change to PUT
        url: "/createfolder" + currentPath,
        data: {
            'folderName': $("#folder-name-input").val()
        },
        success: function () {
            reloadPage();
        }
    });
    return false;
});

$('#folder-modal').on('shown.bs.modal', function () {
    $('#folder-name-input').trigger('focus')
}).on('hidden.bs.modal', function () {
    $('#folder-name-input').val("");
});


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
    $("#upload-files").attr("disabled", true).next(".modal-loading").removeClass("d-none");

    var filesToUpload = $files.map(function () {
        return this.files[0];
    }).get();
    var $progresses = $files.closest(".upload-file-item").find(".upload-file-progress .progress-bar");

    postUpload(filesToUpload, $progresses.get(), function () {
        reloadPage();
    });

    function fileProgress(progress) {
        return function (e) {
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
        if (files.length <= 0) {
            return callback();
        }
        var file = files.pop();
        var progress = progresses.pop();

        var formData = new FormData();
        formData.append("file", file);

        $.ajax({
            type: 'POST',
            url: '/upload' + currentPath,
            data: formData,
            cache: false,
            contentType: false,
            processData: false,
            beforeSend: function (xhr) {
                if (xhr.upload) {
                    xhr.upload.addEventListener('progress', fileProgress(progress), false);
                }
            },
            success: function () {
                $(progress).addClass("bg-success");
                postUpload(files, progresses, callback);
            },
            error: function () {
                $(progress).addClass("bg-danger");
                postUpload(files, progresses, callback);
            }
        });
    }

    return false;
});

$("#upload-modal").on('hidden.bs.modal', function () {
    var $items = $(this).find(".upload-file-item");
    $items.filter(":not(:first-of-type)").remove();
    $items.find("input").val("");
    $items.find("custom-form-control").text("");
    $(".upload-file-loading").addClass("d-none");
});

$("#delete-btn").on('click', function (e) {
    var $checkedFolders = $(".folder-check:checked").closest(".folder-entry");
    var $checkedFiles = $(".file-check:checked").closest(".file-entry");
    var count = $checkedFolders.length + $checkedFiles.length;

    if (count <= 0) return false;

    var $list = $("#delete-list");
    $list.empty();
    $checkedFolders.each(function () {
        var $this = $(this);

        var $li = $('<li class="delete-folder"></li>')
            .data('path', $this.data('path'))
            .text(' ' + $this.data('name'));
        var $input = $('<input type="hidden" name="delete-folder">')
            .val($this.data('path'));

        $li.prepend($('<i class="far fa-folder-open"></i>'));
        $input.appendTo($li);
        $li.appendTo($list);
    });
    $checkedFiles.each(function () {
        var $this = $(this);

        var $li = $('<li class="delete-file"></li>')
            .data('path', $this.data('path'))
            .text(' ' + $this.data('name'));
        var $input = $('<input type="hidden" name="delete-file">')
            .val($this.data('path'));

        $li.prepend($('<i class="far fa-file"></i>'));
        $input.appendTo($li);
        $li.appendTo($list);
    });

    $("#delete-modal").modal('show');
});
var $deleteForm = $("#delete-form");
$deleteForm.on('submit', function () {
    $("#delete-confirm").attr("disabled", true).next(".modal-loading").removeClass("d-none");

    var $folderInputs = $deleteForm.find('input[name=delete-folder]');
    var $fileInputs = $deleteForm.find('input[name=delete-file]');

    var count = $folderInputs.length + $fileInputs.length;

    function descCount() {
        count--;
        if (count == 0) {
            reloadPage();
        }
    }

    var i;
    $folderInputs.each(function () {
        $.ajax({
            type: "DELETE",
            url: "/deletefolder" + this.value
        }).always(descCount);
    });

    $fileInputs.each(function () {
        $.ajax({
            type: "DELETE",
            url: "/deletefile" + this.value
        }).always(descCount);
    });

    return false;
});
