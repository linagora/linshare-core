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
			authentificationFacade.logAuthSuccess(foundUser);
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

	@SuppressWarnings("rawtypes")
	public boolean supports(Class authentication) {
		return (PreAuthenticatedAuthenticationToken.class)
				.isAssignableFrom(authentication);
	}
}
