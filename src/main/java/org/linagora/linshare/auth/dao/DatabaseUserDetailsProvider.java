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
package org.linagora.linshare.auth.dao;

import java.util.List;

import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.facade.auth.AuthentificationFacade;
import org.linagora.linshare.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;

public class DatabaseUserDetailsProvider extends UserDetailsProvider {

	private static final Logger logger = LoggerFactory.getLogger(UserDetailsProvider.class);

	private List<UserRepository<User>> userRepositories;

	public DatabaseUserDetailsProvider(AuthentificationFacade authentificationFacade) {
		super(authentificationFacade);
	}

	public void setUserRepositories(List<UserRepository<User>> userRepositories) {
		this.userRepositories = userRepositories;
	}

	@Override
	public User retrieveUser(String domainIdentifier, String login) {
		User account = null;
		if (domainIdentifier == null) {
			// looking into the database for a user with his login ie username (could be a mail or a LDAP uid)
			try {
				for (UserRepository<User> repository : userRepositories) {
					account = repository.findByLogin(login);
					if (account != null) {
						break;
					}
				}
			} catch (IllegalStateException e) {
				throw new AuthenticationServiceException(
						"Could not authenticate user: " + login);
			}
		} else {
			// check if domain really exist.
			retrieveDomain(login, domainIdentifier);

			// looking in database for a user.
			account = findByLoginAndDomain(domainIdentifier, login);

			if (account == null) {
				List<String> subdomainIdentifiers = authentificationFacade.getAllSubDomainIdentifiers(domainIdentifier);
				for (String subdomainIdentifier : subdomainIdentifiers) {
					account = findByLoginAndDomain(subdomainIdentifier, login);
					if (account != null) {
						logger.debug("User found and authenticated in sub domain "
								+ subdomainIdentifier);
						break;
					}
				}
			}
		}
		return account;
	}

	private User findByLoginAndDomain(String domainIdentifier, String login) {
		User account = null;
		for (UserRepository<User> repository : userRepositories) {
			account = repository.findByLoginAndDomain(domainIdentifier, login);
			if (account != null) {
				break;
			}
		}
		return account;
	}
}