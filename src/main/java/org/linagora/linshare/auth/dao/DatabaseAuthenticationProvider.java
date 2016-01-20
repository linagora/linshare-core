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

import org.linagora.linshare.auth.RoleProvider;
import org.linagora.linshare.core.domain.entities.User;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

public class DatabaseAuthenticationProvider extends
		AbstractUserDetailsAuthenticationProvider {

	// ~ Instance fields
	// ================================================================================================

	private PasswordEncoder passwordEncoder = new PlaintextPasswordEncoder();

	private DatabaseUserDetailsProvider userDetailsProvider;

	public DatabaseAuthenticationProvider(DatabaseUserDetailsProvider userDetailsProvider) {
		super();
		this.userDetailsProvider = userDetailsProvider;
	}

	// ~ Methods
	// ========================================================================================================

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		userDetailsProvider.logAuthSuccess(userDetails.getUsername());
	}

	protected void doAfterPropertiesSet() throws Exception {
		Assert.notNull(this.userDetailsProvider,
				"A userDetailsProvider must be set");
	}

	@Override
	protected final UserDetails retrieveUser(String username,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		UserDetails loadedUser;

		if (username == null || username.length() == 0)
			throw new UsernameNotFoundException("username must not be null");
		logger.debug("Trying to load '" + username + "' account detail ...");

		if (authentication.getCredentials() == null) {
			logger.debug("Authentication failed: no credentials provided");
			throw new BadCredentialsException(messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials",
					"Bad credentials"));
		}

		try {
			String password = null;
			User account = null;
			String domainIdentifier = null;

			// Getting domain from context
			if (authentication.getDetails() != null
					&& authentication.getDetails() instanceof String) {
				domainIdentifier = (String) authentication.getDetails();
			}

			account = userDetailsProvider.retrieveUser(domainIdentifier, username);

			if (account != null) {
				logger.debug("Account in database found : " + account.getAccountRepresentation());
				password = account.getPassword();
				if (password != null && password.equals(""))	password = null;

				// this provider do not manage authentication for internal users.
				if (account.isInternal()) {
					logger.debug("Can not authenticate this user with the current provider : Internal user found");
					throw new UsernameNotFoundException("Account not found");
				}
			}

			if (account == null
					|| password == null
					|| account.hasSystemAccountRole()) {
				logger.debug("Account not found");
				throw new UsernameNotFoundException("Account not found");
			}

			// auth
			String presentedPassword = authentication.getCredentials().toString();

			if (!passwordEncoder.isPasswordValid(password,
					presentedPassword, null)) {
				logger.debug("Authentication failed: password does not match stored value");
				userDetailsProvider.logAuthError(account, "Bad credentials.");
				throw new BadCredentialsException(messages.getMessage(
						"AbstractUserDetailsAuthenticationProvider.badCredentials",
						"Bad credentials"));
			}

			List<GrantedAuthority> grantedAuthorities = RoleProvider.getRoles(account);
			loadedUser = new org.springframework.security.core.userdetails.User(
					account.getLsUuid(), "", true, true, true, true,
					grantedAuthorities);

		} catch (DataAccessException repositoryProblem) {
			throw new AuthenticationServiceException(
					repositoryProblem.getMessage(), repositoryProblem);
		}
		return loadedUser;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
}
