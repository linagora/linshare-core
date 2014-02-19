/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.LdapUserProvider;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.auth.AuthentificationFacade;
import org.linagora.linshare.core.repository.InternalRepository;
import org.linagora.linshare.core.service.UserProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class LdapUserDetailsProvider extends UserDetailsProvider {

	private static final Logger logger = LoggerFactory.getLogger(LdapUserDetailsProvider.class);

	private InternalRepository internalRepository;

	private UserProviderService userProviderService;

	public LdapUserDetailsProvider(AuthentificationFacade authentificationFacade,
			InternalRepository internalRepository,
			UserProviderService userProviderService) {
		super(authentificationFacade);
		this.internalRepository = internalRepository;
		this.userProviderService = userProviderService;
	}

	/**
	 * Looking for an user in the database and then into the LDAP directory.
	 * @param domainIdentifier : could be null.
	 * @param login
	 * @return User account.
	 */
	@Override
	public User retrieveUser(String domainIdentifier, String login) {
		User foundUser = null;
		// if domain was specified at the connection, we try to search the
		// user on this domain and its sub domains.
		try {

			if (domainIdentifier != null) {
				foundUser = findUserInDomainAndSubdomains(login, domainIdentifier);
			} else {
				// There is no constraints, we have to search the current user in
				// all domains.
				foundUser = findUserInAllDomain(login);
			}
			// Check if it is a guest. Should not happen.
			if (!foundUser.isInternal()) {
				logger.debug("Guest found during ldap authentification process.");
				logAuthError(foundUser, domainIdentifier, "User not found.");
				String message = "Guest found : "
						+ foundUser.getAccountReprentation() + " in domain : '"
						+ domainIdentifier + "'";
				logAuthError(login, domainIdentifier, message);
				throw new UsernameNotFoundException(message);
			}

			if (foundUser.getDomain() == null) {
				String message = "Bad credentials";
				logAuthError(foundUser, domainIdentifier, message);
				logger.error("The user found in the database contain a null domain reference.");
				throw new BadCredentialsException("Could not authenticate user: "
						+ login);
			}
		} catch (BusinessException e) {
			logger.error("Couldn't find user during authentication process : "
					+ e.getMessage());
			logAuthError(login, null, e.getMessage());
			throw new AuthenticationServiceException(
					"Could not authenticate user: " + login);
		}
		return foundUser;
	}

	private User findUserInDomainAndSubdomains(String login,
			String domainIdentifier) throws BusinessException {
		User foundUser;
		logger.debug("The domain was specified at the connection time : "
				+ domainIdentifier);
		// check if domain really exist.
		AbstractDomain domain = retrieveDomain(login, domainIdentifier);

		// looking in database for a user.
		foundUser = internalRepository.findByLoginAndDomain(domainIdentifier, login);
		if (foundUser == null) {
			logger.debug("Can't find the user in the DB. Searching in LDAP.");
			// searching in LDAP
			foundUser = userProviderService.searchForAuth(
					domain.getUserProvider(), login);
			if (foundUser != null) {
				// if found we set the domain which belong the user.
				foundUser.setDomain(domain);
			} else {
				Set<AbstractDomain> subdomains = domain.getSubdomain();
				for (AbstractDomain subdomain : subdomains) {
					foundUser = userProviderService.searchForAuth(
							subdomain.getUserProvider(), login);
					if (foundUser != null) {
						// if found we set the domain which belong the user.
						foundUser.setDomain(subdomain);
						logger.debug("User found and authenticated in domain "
								+ subdomain.getIdentifier());
						break;
					}
				}
			}
		}

		if (foundUser == null) {
			String message = "User not found ! Login : '" + login
					+ "' in domain : '" + domainIdentifier + "'";
			logAuthError(login, domainIdentifier, message);
			throw new UsernameNotFoundException(message);
		} else {
			logger.debug("User found in ldap : "
					+ foundUser.getAccountReprentation() + " (domain:"
					+ foundUser.getDomainId() + ")");
		}
		return foundUser;
	}

	private User findUserInAllDomain(String login) throws BusinessException {
		User foundUser = null;
		try {
			foundUser = internalRepository.findByLogin(login);
		} catch (IllegalStateException e) {
			throw new AuthenticationServiceException(
					"Could not authenticate user: " + login);
		}
		if (foundUser != null) {
			// The user was found in the database, we just need to test if he still exists in the LDAP directory
			logger.debug("User found in DB : "
					+ foundUser.getAccountReprentation());
			logger.debug("The user domain stored in DB was : "
					+ foundUser.getDomainId());
			foundUser = userProviderService.searchForAuth(
					foundUser.getDomain().getUserProvider(), login);
		} else {
			logger.debug("Can't find the user in DB. Searching user in all LDAP domains.");
			List<AbstractDomain> domains = authentificationFacade
					.getAllDomains();
			for (AbstractDomain loopedDomain : domains) {
				foundUser = userProviderService.searchForAuth(
							loopedDomain.getUserProvider(), login);
				if (foundUser != null) {
					foundUser.setDomain(loopedDomain);
					logger.debug("User found in domain "
							+ loopedDomain.getIdentifier());
					break;
				}
			}
		}

		if (foundUser == null) {
			String message = "User not found ! Login : " + login;
			logAuthError(login, null, message);
			throw new UsernameNotFoundException("No user found for login: "
					+ message);
		} else {
			logger.debug("User found in ldap : "
					+ foundUser.getAccountReprentation() + " (domain:"
					+ foundUser.getDomainId() + ")");
		}

		return foundUser;
	}

	public User auth(LdapUserProvider userProvider, String login,
			String userPasswd) throws NamingException, IOException , BusinessException {
		return userProviderService.auth(userProvider, login, userPasswd);
	}

	public User findOrCreateUser(String domainIdentifier, String mail) throws BusinessException {
		return authentificationFacade.findOrCreateUser(domainIdentifier, mail);
	}
}