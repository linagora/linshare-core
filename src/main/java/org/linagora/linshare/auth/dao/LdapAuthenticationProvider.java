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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linagora.linshare.auth.RoleProvider;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class LdapAuthenticationProvider extends
		AbstractUserDetailsAuthenticationProvider {

	private final static Log logger = LogFactory
			.getLog(LdapAuthenticationProvider.class);

	private LdapUserDetailsProvider ldapUserDetailsProvider;

	public LdapAuthenticationProvider(LdapUserDetailsProvider ldapUserDetailsProvider) {
		super();
		this.ldapUserDetailsProvider = ldapUserDetailsProvider;
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		ldapUserDetailsProvider.logAuthSuccess(userDetails.getUsername());
	}

	@Override
	protected UserDetails retrieveUser(String login,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {

		UserDetails loadedUser;
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

		try {

			// Getting domain from context
			if (authentication.getDetails() != null
					&& authentication.getDetails() instanceof String) {
				domainIdentifier = (String) authentication.getDetails();
			}

			foundUser = ldapUserDetailsProvider.retrieveUser(domainIdentifier, login);

			try {
				ldapUserDetailsProvider.auth(foundUser.getDomainId(),
						foundUser.getMail(), password);
			} catch (BadCredentialsException e1) {
				logger.debug("Authentication failed: password does not match stored value");
				String message = "Bad credentials.";
				ldapUserDetailsProvider.logAuthError(foundUser, foundUser.getDomainId(), message);
				logger.error(message);
				throw new BadCredentialsException(messages.getMessage(
						"AbstractUserDetailsAuthenticationProvider.badCredentials",
						"Bad credentials"));
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new AuthenticationServiceException(
					"Could not authenticate user : "
					+ foundUser.getDomainId()
					+ " : " + foundUser.getMail(), e);
			}

			User user = null;
			try {
				user = ldapUserDetailsProvider.findOrCreateUser(foundUser.getDomainId(), foundUser.getMail());
			} catch (BusinessException e) {
				logger.error(e);
				throw new AuthenticationServiceException(
						"Could not create user account : "
						+ foundUser.getDomainId() + " : "
						+ foundUser.getMail(), e);
			}

			List<GrantedAuthority> grantedAuthorities = RoleProvider.getRoles(user);
			loadedUser = new org.springframework.security.core.userdetails.User(
					user.getLsUuid(), "", true, true, true, true,
					grantedAuthorities);
		} catch (DataAccessException repositoryProblem) {
			throw new AuthenticationServiceException(
					repositoryProblem.getMessage(), repositoryProblem);
		}
		return loadedUser;
	}
}