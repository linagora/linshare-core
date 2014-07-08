package org.linagora.linshare.view.tapestry.components;

import java.util.List;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.dom.Visitor;

import com.google.common.collect.Lists;

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
			public void visit(Element element) {
				if (element.getName().equalsIgnoreCase("div")) {
					String cssClass = element.getAttribute("class");

					if (cssClass != null && cssClass.equals("t-beaneditor-row")) {
						element.forceAttributes("class", "control-group");
						return;
					}
				}
				if (element.getName().equalsIgnoreCase("label")) {
					element.addClassName("control-label");
					return;
				}
				List<String> inputs = Lists.newArrayList("input", "textarea",
						"select", "checkbox");
				for (String name : inputs) {
					if (element.getName().equalsIgnoreCase(name)) {
						String type = element.getAttribute("type");
						if (type == null || !type.equals("submit")) {
							controls = element.wrap("div", "class", "controls");
						}
						return;
					}
				}
				if (element.getName().equalsIgnoreCase("img")) {
					String cl = element.getAttribute("class");

					if (cl != null && cl.equals("t-calendar-trigger")) {
						element.moveToBottom(controls);
						return;
					}
				}
			}
		});
	}
}
