package org.linagora.linshare.view.tapestry.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

@Import(library = { "filesize/filesize.min.js" })
public class FileSizeDisplay {

	@Parameter(required = true, defaultPrefix = BindingConstants.PROP)
	@Property
	private Long value;

	@Inject
	private Messages messages;
}
