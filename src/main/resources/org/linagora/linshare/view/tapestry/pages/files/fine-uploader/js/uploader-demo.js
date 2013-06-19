var uuids = new Array();

jQuery(document).ready(function() {
    // init
    jQuery("#share").hide();

    // when file are uploading
    var progressHandler = function(id, fileName) {
        jQuery("#share").hide();
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
        if (uuids.length == 0) {
            jQuery("#share").hide();
        }
    };

    // when file uploading is completed
    var completeHandler = function(id, name, responseJSON) {
        if (responseJSON.success) {
            console.log(name + "upload sucesssful");
            uuids.push(uploader.getUuid(id));
        }
        if (uploader.getInProgress() == 0) {
            console.log("All upload are completed");
            jQuery("#share").show();
            jQuery("#uuids").attr("value", uuids.join());
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
