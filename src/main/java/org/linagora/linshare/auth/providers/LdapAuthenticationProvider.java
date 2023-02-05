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
package org.linagora.linshare.auth.providers;

import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.facade.auth.AuthentificationFacade;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.base.Strings;

public class LdapAuthenticationProvider extends DatabaseAuthenticationProvider {

	protected AuthentificationFacade authentificationFacade;

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		if (authentication.getCredentials() == null) {
			logger.debug("Authentication failed: no credentials provided");
			throw new BadCredentialsException(messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials",
					"Bad credentials"));
		}
		String password = authentication.getCredentials().toString();
		// Just to be sure. Providing empty password lead to some issues with Active Directory.
		if (Strings.isNullOrEmpty(password)) {
			logger.debug("Authentication failed: Credentials provided is an empty string");
			throw new BadCredentialsException(messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials",
					"Empty credentials"));
		}
		try {
			/* Some properties are missing from UserDetails (domain id),
			 * so we user userRepository to load the underlying entity.
			 * TODO extend the UserDetails class.
			 */
			User user = userRepository.findByLsUuid(userDetails.getUsername());
			authentificationFacade.ldapAuth(user.getDomainId(), user.getMail(), password);
			checkTOTP(user, authentication);
			logger.debug("Authentication succeded for user " + user.toString());
		} catch (AuthenticationException e) {
			// All other exceptions must be converted to AuthenticationServiceException because it is a failure of the underlying provider.
			logger.debug("Authentication failed.");
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new AuthenticationServiceException("Could not authenticate user : " + userDetails.toString());
		}
	}

	public AuthentificationFacade getAuthentificationFacade() {
		return authentificationFacade;
	}

	public void setAuthentificationFacade(AuthentificationFacade authentificationFacade) {
		this.authentificationFacade = authentificationFacade;
	}
}
