/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
package org.linagora.linshare.auth.jwt;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linagora.linshare.auth.RoleProvider;
import org.linagora.linshare.auth.dao.LdapUserDetailsProvider;
import org.linagora.linshare.auth.sso.SSOAuthenticationProvider;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.auth.AuthentificationFacade;
import org.linagora.linshare.core.service.JwtService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

public class JwtAuthenticationProvider implements AuthenticationProvider {

	private final static Log logger = LogFactory.getLog(SSOAuthenticationProvider.class);

	private AuthentificationFacade authentificationFacade;

	private JwtService jwtService;

	private LdapUserDetailsProvider ldapUserDetailsProvider;

	public void setAuthentificationFacade(AuthentificationFacade authentificationFacade) {
		this.authentificationFacade = authentificationFacade;
	}

	public void setLdapUserDetailsProvider(LdapUserDetailsProvider ldapUserDetailsProvider) {
		this.ldapUserDetailsProvider = ldapUserDetailsProvider;
	}

	public void setJwtService(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		final String token = (String) authentication.getPrincipal();
		Claims claims;
		try {
			claims = jwtService.decode(token);
		} catch (SignatureException e) {
			logger.warn(e.getMessage(), e);
			String msg = String.format("The token is not valid : %1$s : %2$s", e.getMessage(), token);
			throw new AuthenticationServiceException(msg, e);
		} catch (ExpiredJwtException e) {
			logger.warn(e.getMessage(), e);
			// TODO: log authentication failure
//			authentificationFacade.logAuthError(user, message);
			throw new AuthenticationServiceException("The token is expired and not valid anymore : " + token, e);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			String msg = String.format("Invalid token : %1$s : %2$s", e.getMessage(), token);
			throw new AuthenticationServiceException(msg, e);
		}

		User foundUser = null;
		String domainUuid = claims.get("domain", String.class);
		try {
			String email= claims.getSubject();
			String accountUuid = claims.get("accountUuid", String.class);
			// If account uuid, is provided, we use it instead of email.
			if (accountUuid != null) {
				foundUser = authentificationFacade.loadUserDetails(accountUuid);
			} else {
				foundUser = ldapUserDetailsProvider.retrieveUser(domainUuid, email);
			}
		} catch (BusinessException e) {
			logger.error(e.getMessage(), e);
			throw new AuthenticationServiceException("Could not find user account : " + claims, e);
		}
		if (foundUser == null) {
			// if we can find the user with jwt token, the user may not exist.
			// Token is still valid but we can't continue, we have to abord authentification process.
			// We can't return null because there is one and only Provider that can handle JwtAuthenticationToken
			throw new AuthenticationServiceException("Could not find user account : " + claims);
		}
		try {
			// loading /creating the real entity
			foundUser = ldapUserDetailsProvider.findOrCreateUser(foundUser.getDomainId(), foundUser.getMail());
		} catch (BusinessException e) {
			logger.error(e);
			throw new AuthenticationServiceException(
					"Could not create user account : "
					+ foundUser.getDomainId() + " : "
					+ foundUser.getMail(), e);
		}

		authentificationFacade.logAuthSuccess(foundUser);
		List<GrantedAuthority> grantedAuthorities = RoleProvider.getRoles(foundUser);
		UserDetails userDetail = new org.springframework.security.core.userdetails.User(foundUser.getLsUuid(), "", true,
				true, true, true, grantedAuthorities);

		return new UsernamePasswordAuthenticationToken(userDetail, authentication.getCredentials(), grantedAuthorities);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (JwtAuthenticationToken.class).isAssignableFrom(authentication);
	}

}
