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
 *   License along with Linshare.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linshare.view.tapestry.rest;

import java.io.IOException;

import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;

public interface DocumentRestService {

	/**
	 * Return user document list
	 * ->SC_FORBIDDEN if no user is authed
	 * ->SC_NOT_FOUND if the user has no docs
	 * ->SC_OK : with the marshaller doc list
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	public void getdocumentlist(Request request, Response response)
			throws IOException;

	/**
	 * Fetch a file owned by the authed user
	 * -> SC_FORBIDDEN if no user is authed
	 * -> SC_NOT_FOUND if the doc does not belong to the user
	 * -> SC_OK and a application/octet-stream containing the document
	 * @param request : mandatory parameter for rest
	 * @param response : mandatory parameter for rest
	 * @param uuid : the file uuid
	 * @throws IOException : if something goes wrong
	 */
	public void getdocument(Request request, Response response, String uuid)
			throws IOException;

        /**
	 * Remove a file owned by the authed user
	 * -> SC_FORBIDDEN if no user is authed
	 * -> SC_NOT_FOUND if the doc does not belong to the user
	 * -> SC_OK 
	 * @param request : mandatory parameter for rest
	 * @param response : mandatory parameter for rest
	 * @param uuid : the file uuid
	 * @throws IOException : if something goes wrong
	*/
	public void removedocument(Request request, Response response, String uuid)
			throws IOException;


	/**
	 * Expect a post request, multipart encoded
	 * A file, param name file
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	public void uploadfile(Request request, Response response)
			throws IOException;

	
	/**
	 * Find a document using criteria
	 * POST method, read the following fields :
	 * name : file name
	 * type : MIME type
	 * shared : if "true", search for shared doc
	 * sizeMin : min size
	 * sizeMax : max size
	 * documentType : if OWNED : search owned doc; if SHARED : search received doc, else search both
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public void finddocument(Request request, Response response)throws IOException;
	

	/**
	 * Get user free space.
	 * -> SC_FORBIDDEN if no user is authed
	 * -> SC_NOT_FOUND if the doc does not belong to the user
	 * -> SC_OK and the available free spaces in octets
	 * @param request : mandatory parameter for rest
	 * @param response : mandatory parameter for rest
	 * @throws IOException : if something goes wrong
	 */
    void getFreeSpace(Request request, Response response) throws IOException;

	/**
	 * Get maximum file size authorized
	 * -> SC_FORBIDDEN if no user is authed
	 * -> SC_NOT_FOUND if the doc does not belong to the user
	 * -> SC_OK and the maximum file size authorized in octets
	 * @param request : mandatory parameter for rest
	 * @param response : mandatory parameter for rest
	 * @throws IOException : if something goes wrong
	 */
    void getMaxFileSize(Request request, Response response) throws IOException;

    void getdocumentproperties(Request request, Response response,String uid) throws IOException;


}