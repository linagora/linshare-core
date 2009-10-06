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
package org.linagora.linShare.view.tapestry.rest;

import java.io.IOException;

import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;

public interface UserRestService {

	
	/**
	 * Find a user given its email
	 * The authed user must have the upload right to use this method 
	 * Return the marshalled found user, or SC_NOT_FOUND 
	 * 
	 * -> SC_UNAUTHORIZED : if no user is authed
	 * 
	 * @param request : mandatory field
	 * @param response : mandatory field
	 * @param mail : the searched user email
	 * @throws IOException
	 */
	public void getuser(Request request, Response response, String mail)
		throws IOException;
	
	/**
	 * Create an user
	 * The authed user mustn't be a guest to use this method
	 * POST method, need the following fields :
	 * firstName : create user first name
	 * lastName : created user last name
	 * mail : the user email adress, must be a valid email adress
	 * canUpload : true -> the user can upload, else cannot
	 * 
	 * Return are :
	 * ->SC_CREATED and the marshalled created user
	 * ->SC_METHOD_FAILURE if the creation failed 
	 * ->SC_BAD_REQUEST : if a field is missing
	 * ->SC_METHOD_NOT_ALLOWED : if it's not a POST method
	 * ->SC_FORBIDDEN : if the authed user is a guest
	 * -> SC_UNAUTHORIZED : if no user is authed
	 *  
	 * @param request: mandatory field
	 * @param response: mandatory field
	 * @throws IOException
	 */
	public void createuser(Request request, Response response) throws IOException;
	
	/**
	 * Return a list of users whom email match the auto completion
	 * 
	 * -> SC_FORBIDDEN : if the authed user is a guest and cannot upload
	 * -> SC_UNAUTHORIZED : if no user is authed
	 * -> SC_NOT_FOUND : if nothing at all is found
	 * -> SC_OK : success
	 * 
	 * @param request: mandatory field
	 * @param response: mandatory field
	 * @param mail : a part of the users email
	 * @throws IOException
	 */
	public void autoCompleteUser(Request request, Response response, String mail) throws IOException;
	
}
