/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
package org.linagora.linshare.view.tapestry.services;

import java.io.InputStream;
import java.util.Map;

public interface Templating {

	/**
	 * This method build a message from an inputStream and a couple of keys/values
	 * In intern this method read all the content of the template with readFullyTemplateContent(InputStream template)
	 * before calling the template method getMessage(String templateContent, Map<String,String> parameters)
	 * 
	 * @param template the template of the file containing the keys.
	 * @param the map of parameters/values for replace keys in the file. 
	 * @return the message built.
	 */
	public String getMessage(InputStream template,Map<String,String> parameters) ;
	
	
	
	/**
	 * This method build a message from an templateContent and a couple of keys/values
	 * @param templateContent
	 * @param parameters
	 * @return the message built with the values of the keys
	 */
	
	public String getMessage(String templateContent, Map<String,String> parameters);
	
	/**
	 * read a template as a string in memory
	 * @param template the template of the file containing the keys.
	 * @return template content
	 */
	public String readFullyTemplateContent(InputStream template);
	
	
}
