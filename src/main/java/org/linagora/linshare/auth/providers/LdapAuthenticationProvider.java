/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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
