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
package org.linagora.linshare.auth.sso;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linagora.linshare.auth.RoleProvider;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.auth.AuthentificationFacade;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class SSOAuthenticationProvider implements AuthenticationProvider {

	private final static Log logger = LogFactory
			.getLog(SSOAuthenticationProvider.class);

	private AuthentificationFacade authentificationFacade;

	public void setAuthentificationFacade(
			AuthentificationFacade authentificationFacade) {
		this.authentificationFacade = authentificationFacade;
	}

	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {

		// Getting user name from context
		final String userName = (String) authentication.getPrincipal();
		logger.debug("Retrieving user detail for sso authentication with login : "
				+ userName);

		User foundUser = null;
		try {
			foundUser = authentificationFacade.loadUserDetails(userName);
		} catch (BusinessException e) {
			logger.error(e);
			throw new AuthenticationServiceException(
					"Could not find user account : " + userName, e);
		}

		if (foundUser == null) {
			return null;
		}

		try {
			authentificationFacade.logAuthSuccess(foundUser.getLsUuid());
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			logger.debug(e.getStackTrace());
		}

		List<GrantedAuthority> grantedAuthorities = RoleProvider.getRoles(foundUser);
		UserDetails userDetail = new org.springframework.security.core.userdetails.User(
				foundUser.getLsUuid(), "", true, true, true, true,
				grantedAuthorities);

		return new UsernamePasswordAuthenticationToken(userDetail,
				authentication.getCredentials(), grantedAuthorities);
	}

	public boolean supports(Class<?> authentication) {
		return (PreAuthenticatedAuthenticationToken.class)
				.isAssignableFrom(authentication);
	}
}
