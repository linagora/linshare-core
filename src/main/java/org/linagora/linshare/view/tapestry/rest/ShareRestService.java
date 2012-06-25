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
package org.linagora.linshare.view.tapestry.rest;

import java.io.IOException;

import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;


/**
 * Interface for the Share service
 * Allows for creation of a sharing
 * Not CamelCase for the rest service doesn't accept it for some odd reason 
 * @author ncharles
 *
 */
public interface ShareRestService {

	/**
	 * Share a document with a user
	 * Returns are :
	 * -> HttpStatus.SC_UNAUTHORIZED if the user is not authentified
	 * -> HttpStatus.SC_FORBIDDEN if the user is a guest without upload right
	 * -> HttpStatus.SC_NOT_FOUND if either the document or the target user are not found
	 * -> HttpStatus.SC_METHOD_FAILURE if the sharing cannot be created (maybe not a proper return type)
	 * -> HttpStatus.SC_OK if the sharing is successful
	 * @param request : mandatory field for the rest system
	 * @param response : mandatory field for the rest system
	 * @param targetMail : the email of the target
	 * @param uuid : the uuid of the document to be shared
	 * @throws IOException : in case of failure
	 * 
	 */
	public void sharedocument(Request request, Response response,
			String targetMail, String uuid) throws IOException;

	
	/**
	 * Share n documents with a user
	 * POST Methods, expect fields
	 * - targetMail : the adress email of the recipient (which may, or may not exist in the database)
	 * - file : the uuid of the file to be shared
	 * - file1, file2, file3, ... (optionnal) : other uuid of files to be shared
	 * - message (option) : the message to be added in the email
	 * 
	 * Returns are :
	 * -> HttpStatus.SC_UNAUTHORIZED if the user is not authentified
	 * -> HttpStatus.SC_FORBIDDEN if the user is a guest without upload right
	 * -> HttpStatus.SC_METHOD_NOT_ALLOWED if the request is not a POST
	 * -> HttpStatus.SC_NOT_FOUND : if a file is not found
	 * -> HttpStatus.SC_BAD_REQUEST if either the targetMail or the file field is missing
	 * -> HttpStatus.SC_NOT_FOUND if either the document or the target user are not found
	 * -> HttpStatus.SC_METHOD_FAILURE if the sharing cannot be created (maybe not a proper return type)
	 * 
	 * -> HttpStatus.SC_OK if the sharing is successful
	 * @param request : mandatory field for the rest system
	 * @param response : mandatory field for the rest system
	 * @throws IOException : in case of failure
	 * 
	 */
	public void multiplesharedocuments(Request request, Response response) throws IOException;
}