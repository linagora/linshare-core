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
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.AfterRenderBody;
import org.apache.tapestry5.annotations.BeforeRenderBody;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.chenillekit.tapestry.core.base.AbstractWindow;

/**
 * creates a window based on jvascript <a href="http://prototype-window.xilinus.com/">window</a> library.
 *
 * @version $Id: Window.java 682 2008-05-20 22:00:02Z homburgs $
 */
public class Window extends AbstractWindow
{
	@Environmental
	private JavaScriptSupport renderSupport;

	private boolean hasBody = false;

	/**
	 * Tapestry render phase method.
	 * Called before component body is rendered.
	 *
	 * @param writer the markup writer
	 */
	@BeforeRenderBody
	void beforeRenderBody(MarkupWriter writer)
	{
		hasBody = true;
		writer.element("div", "id", getClientId() + "Content", "style", "display:none;");
	}

	/**
	 * Tapestry render phase method.
	 * Called after component body is rendered.
	 * return false to render body again.
	 *
	 * @param writer the markup writer
	 */
	@AfterRenderBody
	void afterRenderBody(MarkupWriter writer)
	{
		writer.end();
	}


	/**
	 * Tapestry render phase method. End a tag here.
	 *
	 * @param writer the markup writer
	 */
	@AfterRender
	void afterRender(MarkupWriter writer)
	{
		JSONObject options = new JSONObject();

		options.put("className", getClassName());
		options.put("width", getWidth());
		options.put("height", getHeight());
		options.put("id", getClientId());
		options.put("title", getTitle());

		//
		// Let subclasses do more.
		//
		configure(options);

		renderSupport.addScript("%s = new Window(%s);", getClientId(), options);

		if (hasBody)
			renderSupport.addScript("%s.setContent('%sContent');", getClientId(), getClientId());

		if (isShow())
			renderSupport.addScript("%s.show%s(%s);", getClientId(), isCenter() ? "Center" : "", isModal());
	}
}