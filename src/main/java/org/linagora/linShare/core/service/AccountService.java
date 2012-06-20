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
package org.linagora.linShare.core.service;

import org.linagora.linShare.core.domain.entities.Account;

/** Services for User management.
 */
public interface AccountService {

	/**
	 * change for accounts with password
	 * @param lsUid
	 * @param oldPassword
	 * @param newPassword
	 * @throws BusinessException : AUTHENTICATION_ERROR if the password supplied is wrong
	 */
//	public void changePassword(String lsUid, String oldPassword, String newPassword) throws BusinessException;
	
	/**
	 * Reset a guest password
	 * @param login
     * @param mailContainer informations needed to construct the email
	 * @throws BusinessException
	 */
//	public void resetPassword(String lsUid, MailContainer mailContainer) throws BusinessException;
	
	/** Find a  user (based on mail address).
     * Search first in database, then on ldap if not found.
     * If the user isn't found on DB, then it is created from the ldap info.
     * If the user isn't found in the ldap, an exception is raised.
     * @param mail user mail.
     * @param domainId domain identifier.
     * @return founded user.
     * @throws BusinessException if the user could not be found
     * @throws TechnicalError if the user cannot be created
     */
//    public Account findOrCreateUser(String mail, String domainId) throws BusinessException ;
    
    
    /**
     * 
     * @param uid user identifier, could be his email or his ldap uid
     * @return
     */
    public Account findUserInDB(String uid);
    
	public Account findUserInDB(String domain, String uid);
    
    /**
	 * This method is designed to search in all existing domains, and create an user entity in the database if it was a successful research(got one only hit)  
	 * @param mail : this parameter should be unique in directories
	 * @return User entity
	 */
//	public Account searchAndCreateUserEntityFromUnkownDirectory(String mail) throws BusinessException;
}
