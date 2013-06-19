var uuids = new Array();
var submit = false;

var toggleSubmitOn = function() {
    submit = true;
};

jQuery(document).ready(function() {
    // init
    jQuery("#share").hide();

    // when file are uploading
    var progressHandler = function(id, fileName) {
        console.log("progressHandler: A new upload is in progress");
    };

    // when server error
    var errorHandler = function(id, fileName, reason) {
        qq.log("id: " + id + ", fileName: " + fileName + ", reason: " + reason);
    };

    // when asking for a deletion
    var deleteHandler = function(id) {
        // remove item from list of uploaded items to share
        uuids.splice(uuids.indexOf(uploader.getUuid(id)), 1);
    };

    // when file uploading is completed
    var completeHandler = function(id, name, responseJSON) {
        if (responseJSON.success) {
            console.log(name + "upload sucesssful");
            uuids.push(uploader.getUuid(id));
        }
        if (uploader.getInProgress() == 0) {
            console.log("All upload are completed");
            jQuery("#uuids").attr("value", uuids.join());
            if (submit) {
                setTimeout(function() {
                    jQuery('#shareform').submit();
                    submit = false;
                }, 3000);
            }
        }
    };

    var uploader = new qq.FineUploader({
        element: jQuery('#uploader')[0],
        debug: true,
        request: {
            endpoint: "/linshare/webservice/fineuploader/upload/receiver"
        },
        callbacks: {
            onComplete: completeHandler,
            onDelete: deleteHandler,
            onError: errorHandler,
            onProgress: progressHandler
        },
        deleteFile: {
            enabled: true,
            forceConfirm: true,
            endpoint: "/linshare/webservice/fineuploader/upload/receiver"
        }
    });
});
