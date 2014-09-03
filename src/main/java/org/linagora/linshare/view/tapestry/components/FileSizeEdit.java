package org.linagora.linshare.view.tapestry.components;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.base.AbstractTextField;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

@Import(library = { "filesize/filesize.min.js", "FileSizeEdit.js" })
public class FileSizeEdit extends AbstractTextField {

	@Property
	private String name;

	@Inject
	private Messages messages;

	@Inject
	private ComponentResources componentResources;

	@Override
	protected void writeFieldTag(MarkupWriter writer, String value) {
		writer.element("input",
				       "type", "number",
				       "name", getControlName(),
				       "id", getClientId(),
				       "value", value,
				       "size", getWidth());
	}

	final void beginRender(MarkupWriter writer) {
		String opt = String.format(
				"{ suffixes : { B: '%s', kB: '%s', MB: '%s', GB: '%s' } }",
				messages.get("global.byte"), messages.get("global.kilobyte"),
				messages.get("global.megabyte"),
				messages.get("global.gigabyte"));
		String span = String
				.format("<script type=\"text/javascript\">getFileSize('%s', %s);</script>",
						getClientId(), opt);

		writer.end();
		writer.element("span").raw(span);
		writer.end();
	}
}
