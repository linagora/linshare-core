/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.service;

import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;

import javax.annotation.Nonnull;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserLdapPattern;
import org.linagora.linshare.core.exception.BusinessException;

public interface LDAPUserQueryService {

	public User auth(LdapConnection ldapConnection, String baseDn, UserLdapPattern domainPattern, String userLogin, String userPasswd) throws NamingException, IOException;

	public User searchForAuth(LdapConnection ldapConnection, String baseDn, UserLdapPattern domainPattern, String userLogin) throws NamingException, IOException;

	public List<User> searchUser(LdapConnection ldapConnection, String baseDn, UserLdapPattern domainPattern, String mail, String firstName, String lastName) throws BusinessException, NamingException,
			IOException;

	public Boolean isUserExist(LdapConnection ldapConnection, String baseDn, UserLdapPattern domainPattern, String mail) throws BusinessException, NamingException, IOException;
	
	public User getUser(LdapConnection ldapConnection, String baseDn, UserLdapPattern domainPattern, String mail) throws BusinessException, NamingException,	IOException;

	public User getUserByUid(LdapConnection ldapConnection, String baseDn, UserLdapPattern domainPattern, @Nonnull String externalUid) throws BusinessException, NamingException,	IOException;


	public List<User> completeUser(LdapConnection ldapConnection, String baseDn, UserLdapPattern domainPattern, String pattern) throws BusinessException, NamingException, IOException;

	public List<User> completeUser(LdapConnection ldapConnection, String baseDn, UserLdapPattern domainPattern, String firstName, String lastName) throws BusinessException, NamingException, IOException;
}
