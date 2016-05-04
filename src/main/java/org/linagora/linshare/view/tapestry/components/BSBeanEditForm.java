/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */

package org.linagora.linshare.view.tapestry.components;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.dom.Visitor;

import com.google.common.collect.Sets;

@Import(library = { "bootstrap/js/bootstrap.js", "filesize/filesize.min.js" })
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
				} else if (is(e, "span")) {
					e.moveToBottom(controls);
					e.addClassName("help-block");
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
