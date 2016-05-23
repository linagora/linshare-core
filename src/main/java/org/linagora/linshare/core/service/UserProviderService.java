/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.service;

import java.util.List;

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

	User findUser(UserProvider userProvider, String mail) throws BusinessException;
	Boolean isUserExist(UserProvider userProvider, String mail) throws BusinessException;

	List<User> searchUser(UserProvider userProvider, String mail, String firstName, String lastName) throws BusinessException;

	List<User> autoCompleteUser(UserProvider userProvider, String pattern) throws BusinessException;
	List<User> autoCompleteUser(UserProvider userProvider, String firstName, String lastName) throws BusinessException;

	User auth(UserProvider userProvider, String login, String userPasswd) throws BusinessException;
	User searchForAuth(UserProvider userProvider, String login) throws BusinessException;

	public boolean canDeletePattern(String uuid);
}
