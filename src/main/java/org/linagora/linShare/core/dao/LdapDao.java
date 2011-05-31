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
package org.linagora.linShare.core.dao;

import java.util.List;
import java.util.Map;

import org.linagora.linShare.core.dao.ldap.LdapSearchResult;
import org.linagora.linShare.core.domain.entities.User;

/**
 * @deprecated not used anymore since domain implementation
 */
public interface LdapDao {
	/**
	 * This method permits to know if key exists in a data source
	 * @param key the key to retrieve
	 * @return true if the key exists in the data source
	 */
	public boolean exist(String... keys);
	
	/**
	 * Retrieve attributes values from keys 
	 * @param keys params to identify the results
	 * @param attributes
	 */
	public Map<String,String> getValues(List<String> keys ,String...attributes );
	
	
	/**
	 * Retrieve UserPassword (this is needed if we want the salt, last four bytes)
	 * @param login keys to compose login (mail for example)
	 */
	public byte[] getUserPassword(String... keys);
	
	
	/**
	 * check existing entry with the given keys
	 * and the associated password
	 * @param password the password.
	 * @param keys like exists for filtering an entry
	 * @return true if entry and password is ok.
	 */
	public boolean auth(String password, String... keys);

    /** Search a user (near match search).
     * @param mail user mail.
     * @param firstName user first name.
     * @param lastName user last name.
     * @return list of users.
     */
    LdapSearchResult<User> searchUser(String mail, String firstName, String lastName);

    /** Search a user (exact match search).
     * @param mail user mail.
     * @return founded user.
     */
    User searchUser(String mail);
    
    /** Search a user (contains match search).
     * @param mail user mail.
     * @param firstName user first name.
     * @param lastName user last name.
     * @return list of users.
     */
    public LdapSearchResult<User> searchUserAnyWhere(String mail, String firstName, String lastName);
    
    
    public boolean isSearchResultTrucated(int listSize);

	public User searchUserWithUid(String uid);
    
}

