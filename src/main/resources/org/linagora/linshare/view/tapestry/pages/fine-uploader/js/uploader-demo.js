jQuery(document).ready(function() {
    var errorHandler = function(event, id, fileName, reason) {
        qq.log("id: " + id + ", fileName: " + fileName + ", reason: " + reason);
    };

    var uploader = new qq.FineUploader({
        element: jQuery('#basicUploadSuccessExample')[0],
        debug: true,
        request: {
            endpoint: "/linshare/webservice/fineuploader/upload/receiver"
        },
        callbacks: {
            onError: errorHandler
        },
        deleteFile: {
            enabled: true,
            endpoint: "/linshare/webservice/fineuploader/upload/receiver"
        }
    });



    var uploader2 = new qq.FineUploader({
        element: jQuery('#manualUploadModeExample')[0],
        autoUpload: false,
        uploadButtonText: "Select Files",
        request: {
            endpoint: "/linshare/webservice/fineuploader/upload/receiver"
        },
        callbacks: {
            onError: errorHandler
        }
    });

    jQuery('#triggerUpload').click(function() {
        uploader2.uploadStoredFiles();
    });


    var uploader3 = new qq.FineUploader({
        element: jQuery('#basicUploadFailureExample')[0],
        callbacks: {
            onError: errorHandler
        },
        request: {
            endpoint: "/linshare/webservice/fineuploader/upload/receiver",
            params: {"generateError": true}
        },
        failedUploadTextDisplay: {
            mode: 'custom',
            maxChars: 5
        }
    });


    var uploader4 = new qq.FineUploader({
        element: jQuery('#uploadWithVariousOptionsExample')[0],
        multiple: false,
        request: {
            endpoint: "/linshare/webservice/fineuploader/upload/receiver"
        },
        validation: {
            allowedExtensions: ['jpeg', 'jpg', 'txt'],
            sizeLimit: 50000
        },
        text: {
            uploadButton: "Click Or Drop"
        },
        callbacks: {
            onError: errorHandler
        }
    });

    uploader5 = new qq.FineUploaderBasic({
        multiple: false,
        autoUpload: false,
        button: jQuery("#fubUploadButton")[0],
        request: {
            endpoint: "/linshare/webservice/fineuploader/upload/receiver"
        },
        callbacks: {
            onError: errorHandler
        }
    });
});
