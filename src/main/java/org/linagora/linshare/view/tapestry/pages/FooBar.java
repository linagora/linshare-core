package org.linagora.linshare.view.tapestry.pages;

import org.apache.tapestry5.annotations.Import;

@Import(
		library = {
				"../components/jquery/jquery-1.7.2.js",
                "../components/fineuploader/fineuploader-3.6.4.js",
				"./fine-uploader/js/uploader-demo.js"
		},
		stylesheet={
				"../components/fineuploader/fineuploader-3.6.4.css",
				"./fine-uploader/css/styles.css"
		})
public class FooBar {
}
