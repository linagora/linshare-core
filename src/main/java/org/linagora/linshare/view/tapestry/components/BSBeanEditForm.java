package org.linagora.linshare.view.tapestry.components;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.dom.Visitor;

import com.google.common.collect.Sets;

@Import(library = { "bootstrap/js/bootstrap.js" })
@SupportsInformalParameters
public class BSBeanEditForm extends BeanEditForm {

	@Import(stylesheet = { "bootstrap/css/bootstrap.css" })
	void cleanupRender() {
	}

	/*
	 * XXX: dom rewriting
	 */
	void afterRender(MarkupWriter writer) throws Exception {
		Element element = writer.getElement().getElementByAttributeValue(
				"class", "well");
		element.visit(new Visitor() {
			private Element controls;

			@Override
			public void visit(Element e) {
				if (is(e, "div") && has(e, "class", "t-beaneditor-row")) {
					e.forceAttributes("class", "control-group");
				} else if (is(e, "label")) {
					e.addClassName("control-label");
				} else if (isInput(e) && !has(e, "type", "submit")) {
					controls = e.wrap("div", "class", "controls");
				} else if (is(e, "img")
						&& has(e, "class", "t-calendar-trigger")) {
					e.moveToBottom(controls);
				}
			}
		});
	}

	/*
	 * Helpers
	 */
	private boolean is(Element e, String tag) {
		return e.getName().equalsIgnoreCase(tag);
	}

	private boolean isInput(Element e) {
		return Sets.newHashSet("input", "textarea", "select", "checkbox")
				.contains(e.getName().toLowerCase());
	}

	private boolean has(Element e, String attr, String val) {
		return String.valueOf(e.getAttribute(attr)).equals(val);
	}
}
