package org.linagora.linshare.view.tapestry.pages.files;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

@Import(
		library = {
				"../../components/jquery/jquery-1.7.2.js",
                "../../components/fineuploader/fineuploader-3.6.4.js",
				"./fine-uploader/js/uploader-demo.js"
		},
		stylesheet={
				"../../components/fineuploader/fineuploader-3.6.4.css",
				"./fine-uploader/css/styles.css"
		})
public class Upload {

	@Inject
	private Messages messages;

	@Persist("flash")
	@Property
	private String uuids;
	
}
