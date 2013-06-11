package org.linagora.linshare.view.tapestry.pages;

import org.apache.tapestry5.annotations.Import;

@Import(
		library = {
				"../components/jquery/jquery-1.7.2.js",
				"./fine-uploader/client/js/header.js",
				"./fine-uploader/client/js/util.js",
				"./fine-uploader/client/js/features.js",
				"./fine-uploader/client/js/promise.js",
				"./fine-uploader/client/js/button.js",
				"./fine-uploader/client/js/ajax.requester.js",
				"./fine-uploader/client/js/deletefile.ajax.requester.js",
				"./fine-uploader/client/js/handler.base.js",
				"./fine-uploader/client/js/handler.base.js",
				"./fine-uploader/client/js/window.receive.message.js",
				"./fine-uploader/client/js/handler.form.js",
				"./fine-uploader/client/js/handler.xhr.js",
				"./fine-uploader/client/js/paste.js",
				"./fine-uploader/client/js/uploader.basic.js",
				"./fine-uploader/client/js/dnd.js",
				"./fine-uploader/client/js/uploader.js",
				"./fine-uploader/js/uploader-demo.js"
		},
		stylesheet={
				"./fine-uploader/client/fineuploader.css",
				"./fine-uploader/css/styles.css"
		})
public class FooBar {
}
