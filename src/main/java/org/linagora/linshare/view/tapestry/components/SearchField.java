package org.linagora.linshare.view.tapestry.components;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.corelib.components.TextField;

public class SearchField extends TextField {
	
	private static final String TYPE = "search";
	
	private static final int MAX = 5;

	@Override
	protected void writeFieldTag(MarkupWriter writer, String value) {
		writer.element("input",
				"type", TYPE,
				"result", MAX,
				"name", getControlName(),
				"id", getClientId(),
				"value", value,
				"size", getWidth());
	}
}
