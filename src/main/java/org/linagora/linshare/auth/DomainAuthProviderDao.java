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
package org.linagora.linshare.auth;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linagora.linshare.auth.exceptions.BadDomainException;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserLogEntry;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.UserProviderService;
import org.linagora.linshare.core.service.UserService;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

public class DomainAuthProviderDao extends
		AbstractUserDetailsAuthenticationProvider {

	private UserService userService;
	private UserRepository<User> userRepository;
	private AbstractDomainService abstractDomainService;
	private LogEntryService logEntryService;
	private UserProviderService userProviderService;

	private final static Log logger = LogFactory
			.getLog(DomainAuthProviderDao.class);

	public AbstractDomainService getAbstractDomainService() {
		return abstractDomainService;
	}

	public void setAbstractDomainService(
			AbstractDomainService abstractDomainService) {
		this.abstractDomainService = abstractDomainService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public UserRepository<User> getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository<User> userRepository) {
		this.userRepository = userRepository;
	}

	public LogEntryService getLogEntryService() {
		return logEntryService;
	}

	public void setLogEntryService(LogEntryService logEntryService) {
		this.logEntryService = logEntryService;
	}

	public UserProviderService getUserProviderService() {
		return userProviderService;
	}

	public void setUserProviderService(UserProviderService userProviderService) {
		this.userProviderService = userProviderService;
	}

	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
	}

	protected UserDetails retrieveUser(String login,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		logger.debug("Retrieving user detail for ldap authentication with login : "
				+ login);

		User foundUser = null;
		String domainIdentifier = null;

		// Getting password from context
		String password = (String) authentication.getCredentials();
		if (password.isEmpty()) {
			String message = "User password is empty, authentification failed";
			logAuthError(login, domainIdentifier, message);
			logger.error(message);
			throw new BadCredentialsException(messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials",
					"Bad credentials"));
		}

		// Getting domain from context
		if (authentication.getDetails() != null
				&& authentication.getDetails() instanceof String) {
			domainIdentifier = (String) authentication.getDetails();
		}

		// if domain was specified at the connection, we try to authenticate the
		// user on this domain and its sub domains.
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

		try {
			userProviderService.auth(foundUser.getDomain().getUserProvider(),
					foundUser.getMail(), password);
		} catch (BadCredentialsException e1) {
			String message = "Bad credentials.";
			logAuthError(foundUser, foundUser.getDomainId(), message);
			logger.error(message);
			throw new BadCredentialsException("Could not authenticate user: "
					+ login);
		} catch (Exception e) {
			logger.error(e);
			throw new AuthenticationServiceException(
					"Could not authenticate user : " + foundUser.getDomainId()
							+ " : " + foundUser.getMail(), e);
		}

		User user = null;
		try {
			user = userService.findOrCreateUser(foundUser.getMail(),
					foundUser.getDomainId());
		} catch (BusinessException e) {
			logger.error(e);
			throw new AuthenticationServiceException(
					"Could not create user account : "
							+ foundUser.getDomainId() + " : "
							+ foundUser.getMail(), e);
		}

		List<GrantedAuthority> grantedAuthorities = RoleProvider.getRoles(user);
		return new org.springframework.security.core.userdetails.User(
				user.getLsUuid(), "", true, true, true, true,
				grantedAuthorities);
	}

	private User findUserInDomainAndSubdomains(String login,
			String domainIdentifier) {
		User foundUser;
		logger.debug("The domain was specified at the connection time : "
				+ domainIdentifier);
		// check if domain really exist.
		AbstractDomain domain = retrieveDomain(login, domainIdentifier);

		// looking in database for a user.
		foundUser = userRepository.findByLoginAndDomain(domainIdentifier, login);
		if (foundUser == null) {
			logger.debug("Can't find the user in the DB. Searching in LDAP.");
			// searching in LDAP
			try {
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
			} catch (NamingException e) {
				logger.error("Couldn't find user during authentication process : "
						+ e.getMessage());
				logAuthError(login, domainIdentifier, e.getMessage());
				throw new AuthenticationServiceException(
						"Could not authenticate user: " + login);
			} catch (IOException e) {
				logger.error("Couldn't find user during authentication process : "
						+ e.getMessage());
				logAuthError(login, domainIdentifier, e.getMessage());
				throw new AuthenticationServiceException(
						"Could not authenticate user: " + login);
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

	private User findUserInAllDomain(String login) {
		User foundUser = userRepository.findByLogin(login);
		if (foundUser != null) {
			// The user was found in the database, we just need to auth against
			// LDAP.
			logger.debug("User found in DB : "
					+ foundUser.getAccountReprentation());
			logger.debug("The user domain stored in DB was : "
					+ foundUser.getDomainId());
		} else {
			logger.debug("Can't find the user in DB. Searching user in all LDAP domains.");
			List<AbstractDomain> domains = abstractDomainService
					.getAllDomains();
			for (AbstractDomain loopedDomain : domains) {
				try {
					foundUser = userProviderService.searchForAuth(
							loopedDomain.getUserProvider(), login);
				} catch (NamingException e) {
					logger.error("Couldn't find user during authentication process : "
							+ e.getMessage());
					logAuthError(login, null, e.getMessage());
					throw new AuthenticationServiceException(
							"Could not authenticate user: " + login);
				} catch (IOException e) {
					logger.error("Couldn't find user during authentication process : "
							+ e.getMessage());
					logAuthError(login, null, e.getMessage());
					throw new AuthenticationServiceException(
							"Could not authenticate user: " + login);
				}
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

	private AbstractDomain retrieveDomain(String login, String domainIdentifier) {
		AbstractDomain domain = abstractDomainService
				.retrieveDomain(domainIdentifier);
		if (domain == null) {
			logger.error("Can't find the specified domain : "
					+ domainIdentifier);
			logAuthError(login, domainIdentifier, "Bad domain.");
			throw new BadDomainException("Domain '" + domainIdentifier
					+ "' not found", domainIdentifier);
		}
		return domain;
	}

	private void logAuthError(String login, String domainIdentifier,
			String message) {
		try {
			logEntryService.create(new UserLogEntry(login, domainIdentifier,
					LogAction.USER_AUTH_FAILED, message));
		} catch (IllegalArgumentException e) {
			logger.error("Couldn't log an authentication failure : " + message);
			logger.debug(e.getMessage());
		} catch (BusinessException e1) {
			logger.error("Couldn't log an authentication failure : " + message);
			logger.debug(e1.getMessage());
		}
	}

	private void logAuthError(User user, String domainIdentifier, String message) {
		try {
			logEntryService.create(new UserLogEntry(user,
					LogAction.USER_AUTH_FAILED, message, user));
		} catch (IllegalArgumentException e) {
			logger.error("Couldn't log an authentication failure : " + message);
			logger.debug(e.getMessage());
		} catch (BusinessException e1) {
			logger.error("Couldn't log an authentication failure : " + message);
			logger.debug(e1.getMessage());
		}
	}

	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {

		Assert.isInstanceOf(
				UsernamePasswordAuthenticationToken.class,
				authentication,
				messages.getMessage(
						"AbstractUserDetailsAuthenticationProvider.onlySupports",
						"Only UsernamePasswordAuthenticationToken is supported"));

		// Determine username
		String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED"
				: authentication.getName();

		boolean cacheWasUsed = true;
		UserDetails user = this.getUserCache().getUserFromCache(username);

		if (user == null) {
			cacheWasUsed = false;

			try {
				user = retrieveUser(username,
						(UsernamePasswordAuthenticationToken) authentication);
			} catch (UsernameNotFoundException notFound) {
				if (hideUserNotFoundExceptions) {
					throw new BadCredentialsException(
							messages.getMessage(
									"AbstractUserDetailsAuthenticationProvider.badCredentials",
									"Bad credentials"));
				} else {
					throw notFound;
				}
			}

			Assert.notNull(user,
					"retrieveUser returned null - a violation of the interface contract");
		}

		this.getPreAuthenticationChecks().check(user);

		try {
			additionalAuthenticationChecks(user,
					(UsernamePasswordAuthenticationToken) authentication);
		} catch (AuthenticationException exception) {
			if (cacheWasUsed) {
				// There was a problem, so try again after checking
				// we're using latest data (ie not from the cache)
				cacheWasUsed = false;
				user = retrieveUser(username,
						(UsernamePasswordAuthenticationToken) authentication);
				additionalAuthenticationChecks(user,
						(UsernamePasswordAuthenticationToken) authentication);
			} else {
				throw exception;
			}
		}

		this.getPostAuthenticationChecks().check(user);

		if (!cacheWasUsed) {
			this.getUserCache().putUserInCache(user);
		}

		Object principalToReturn = user;

		if (this.isForcePrincipalAsString()) {
			principalToReturn = user.getUsername();
		}

		return createSuccessAuthentication(principalToReturn, authentication,
				user);
	}

}
