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

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.LdapUserProvider;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserLdapPattern;
import org.linagora.linshare.core.domain.entities.UserProvider;
import org.linagora.linshare.core.exception.BusinessException;

public interface UserProviderService {

	List<UserLdapPattern> findAllDomainPattern() throws BusinessException;
	UserLdapPattern findDomainPattern(String uuid) throws BusinessException;
	List<UserLdapPattern> findAllUserDomainPattern() throws BusinessException;
	List<UserLdapPattern> findAllSystemDomainPattern() throws BusinessException;
	UserLdapPattern createDomainPattern(Account actor, UserLdapPattern domainPattern) throws BusinessException;
	UserLdapPattern updateDomainPattern(Account actor, UserLdapPattern domainPattern) throws BusinessException;
	UserLdapPattern deletePattern(Account actor, String patternToDelete) throws BusinessException;

	LdapUserProvider find(String uuid) throws BusinessException;
	boolean exists(String uuid);
	LdapUserProvider create(LdapUserProvider userProvider) throws BusinessException;
	LdapUserProvider update(LdapUserProvider userProvider) throws BusinessException;
	void delete(UserProvider userProvider) throws BusinessException;

	User findUser(AbstractDomain domain, UserProvider userProvider, String mail) throws BusinessException;

	User findUserByExternalUid(AbstractDomain domain, UserProvider userProvider, @NotNull String externalUid) throws BusinessException;
	Boolean isUserExist(AbstractDomain domain, UserProvider userProvider, String mail) throws BusinessException;

	List<User> searchUser(AbstractDomain domain, UserProvider userProvider, String mail, String firstName, String lastName) throws BusinessException;

	List<User> autoCompleteUser(AbstractDomain domain, UserProvider userProvider, String pattern) throws BusinessException;
	List<User> autoCompleteUser(AbstractDomain domain, UserProvider userProvider, String firstName, String lastName) throws BusinessException;

	User auth(UserProvider userProvider, String login, String userPasswd) throws BusinessException;
	User searchForAuth(AbstractDomain domain, UserProvider userProvider, String login) throws BusinessException;

	public boolean canDeletePattern(String uuid);
	List<AbstractDomain> findAllDomainsByUserFilter(Account authUser, UserLdapPattern domainUserFilter);
}
