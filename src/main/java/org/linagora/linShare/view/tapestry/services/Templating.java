/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.view.tapestry.services;

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
