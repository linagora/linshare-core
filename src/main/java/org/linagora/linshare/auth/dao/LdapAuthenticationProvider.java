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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linagora.linshare.auth.RoleProvider;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AccountFacade;
import org.linagora.linshare.core.service.UserProviderService;
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

public class LdapAuthenticationProvider extends
		AbstractUserDetailsAuthenticationProvider {

	private final static Log logger = LogFactory
			.getLog(LdapAuthenticationProvider.class);
	
	private AccountFacade accountFacade;

	private UserProviderService userProviderService;
	
	private LdapUserDetailsProvider ldapUserDetailsProvider;

	public AccountFacade getAccountFacade() {
		return accountFacade;
	}

	public void setAccountFacade(AccountFacade accountFacade) {
		this.accountFacade = accountFacade;
	}

	public void setUserProviderService(UserProviderService userProviderService) {
		this.userProviderService = userProviderService;
	}

	public void setLdapUserDetailsProvider(LdapUserDetailsProvider userDetailsProvider) {
		this.ldapUserDetailsProvider = userDetailsProvider;
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
			ldapUserDetailsProvider.logAuthError(login, domainIdentifier, message);
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

		foundUser = ldapUserDetailsProvider.retrieveUser(domainIdentifier, login);

		try {
			userProviderService.auth(foundUser.getDomain().getUserProvider(),
					foundUser.getMail(), password);
		} catch (BadCredentialsException e1) {
			logger.debug("Authentication failed: password does not match stored value");
			String message = "Bad credentials.";
			ldapUserDetailsProvider.logAuthError(foundUser, foundUser.getDomainId(), message);
			logger.error(message);
			throw new BadCredentialsException(messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials",
					"Bad credentials"), foundUser);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new AuthenticationServiceException(
				"Could not authenticate user : "
				+ foundUser.getDomainId()
				+ " : " + foundUser.getMail(), e);
		}

		User user = null;
		try {
			user = accountFacade.findOrCreateUser(foundUser.getDomainId(), foundUser.getMail());
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
