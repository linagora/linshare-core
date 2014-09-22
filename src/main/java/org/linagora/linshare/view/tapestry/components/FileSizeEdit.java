/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
