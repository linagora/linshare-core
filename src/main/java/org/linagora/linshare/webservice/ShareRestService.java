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
package org.linagora.linshare.webservice;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Path;

import org.linagora.linshare.webservice.dto.ShareDto;


/**
 * Interface for the Share service
 * REST jaxRS interface
 * Allows for creation of a sharing
 */

@Path("/rest/share")
public interface ShareRestService {

	/**
	 * Share a document with a user
	 * Returns are :
	 * -> HttpStatus.SC_UNAUTHORIZED if the user is not authentified
	 * -> HttpStatus.SC_FORBIDDEN if the user is a guest without upload right
	 * -> HttpStatus.SC_NOT_FOUND if either the document or the target user are not found
	 * -> HttpStatus.SC_METHOD_FAILURE if the sharing cannot be created (maybe not a proper return type)
	 * -> HttpStatus.SC_OK if the sharing is successful
	 * @param targetMail : the email of the target
	 * @param uuid : the uuid of the document to be shared
	 * @throws IOException : in case of failure
	 * 
	 */
	public void sharedocument(String targetMail, String uuid, int securedShare);
	
	

	public void multiplesharedocuments(String targetMail, List<String> uuid, int securedShare, String message) ;
	
	
//	public List<ShareDto> getMyOwnShares();
	public List<ShareDto> getReceivedShares();
	
	
}