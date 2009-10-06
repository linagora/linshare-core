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
package org.linagora.linShare.core.repository;


import java.util.List;

import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.exception.BusinessException;

public interface UserRepository<T extends User> extends AbstractRepository<T> {

	
	
    /** 
     * Find a user using its login.
     * @param login
     * @return founded user (null if no user found).
     */
    T findByLogin(String login);

    /** Find a user using its mail.
     * @param mail
     * @return founded user (null if no user found).
     */
    T findByMail(String mail);
    
    /**
     * authentification
     * @param login the login of the user.
     * @param password the password of the user.
     * @return true if the user is matched else false.
     */
    boolean exist(String login, String encPassword);

   
    /**
     * Return a list of mails beginning with the text
     * @param beginWith
     * @return
     */
    List<String> findMails(String beginWith);
    
    /**
     * Remove the document represented by its uuid for the user 
     * @param login of user
     * @param uuid of document
     */
    public void removeOwnerDocumentForUser(String login,String uuid) throws BusinessException;
    
    /**
     * Remove a file sent by the user matches by his login.
     * @param login the login of the user.
     * @param uuid the uuid of the document.
     */
    public void removeSentDocument(String login,String uuid) throws BusinessException;
    
    /**
     * Remove a file received by the user matches by his login.
     * @param login the login of the user.
     * @param uuid the uuid of the document.
     */
    public void removeReceivedDocument(String login,String uuid) throws BusinessException;
     
    
} 
