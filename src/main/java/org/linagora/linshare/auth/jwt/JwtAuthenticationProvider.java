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
package org.linagora.linshare.auth.jwt;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.linagora.linshare.auth.RoleProvider;
import org.linagora.linshare.auth.exceptions.JwtBadFormatException;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.auth.AuthentificationFacade;
import org.linagora.linshare.core.service.JwtService;
import org.linagora.linshare.mongo.entities.PermanentToken;
import org.linagora.linshare.mongo.repository.JwtLongTimeMongoRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.google.common.collect.Lists;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;

public class JwtAuthenticationProvider implements AuthenticationProvider {

	private final static Logger logger = LogManager.getLogger(JwtAuthenticationProvider.class);

	private AuthentificationFacade authentificationFacade;

	private JwtService jwtService;

	private JwtLongTimeMongoRepository jwtLongTimeMongoRepository;

	public void setAuthentificationFacade(AuthentificationFacade authentificationFacade) {
		this.authentificationFacade = authentificationFacade;
	}

	public void setJwtService(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	public void setJwtLongTimeMongoRepository(JwtLongTimeMongoRepository jwtLongTimeMongoRepository) {
		this.jwtLongTimeMongoRepository = jwtLongTimeMongoRepository;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		JwtAuthenticationToken jwtAuthentication = (JwtAuthenticationToken) authentication;
		final String token = jwtAuthentication.getToken();
		Claims claims;
		try {
			claims = jwtService.decode(token);
		} catch (SignatureException e) {
			logger.warn(e.getMessage(), e);
			String msg = String.format("The token is not valid : %1$s : %2$s", e.getMessage(), token);
			throw new JwtBadFormatException(msg, e);
		} catch (ExpiredJwtException e) {
			logger.warn(e.getMessage(), e);
			throw new JwtBadFormatException("The token is expired and not valid anymore : " + token, e);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			String msg = String.format("Invalid token : %1$s : %2$s", e.getMessage(), token);
			throw new AuthenticationServiceException(msg, e);
		}

		if (StringUtils.isBlank(claims.getSubject()) || (claims.getExpiration() == null && !claims.containsKey("uuid"))
				|| claims.getIssuedAt() == null) {
			String msg = String.format("Subject and expiration date are mandatory fields for jwt token: %1$s", token);
			logger.warn(msg);
			throw new AuthenticationServiceException(msg);
		}

		Date issuedAt = claims.getIssuedAt();
		if (issuedAt.after(new Date())) {
			String msg = "Issued date (iat) can not be in the futur for jwt token: %1$s";
			// do not leak token in logs, too dangerous
			logger.warn(msg);
			throw new AuthenticationServiceException(String.format(msg, token));
		}

		if (claims.getExpiration() == null && claims.containsKey("uuid") && claims.get("uuid") != null) {
			PermanentToken jwtLongTime = jwtLongTimeMongoRepository.findByUuid(claims.get("uuid", String.class));
			if (jwtLongTime == null) {
				String message = "No valid JWT permanent token found for this token : %1$s";
				logger.warn(String.format(message, claims.get("uuid")));
				jwtAuthentication.setSubject(claims.get("sub", String.class));
				throw new JwtBadFormatException(String.format(message, token));
			} else if (!jwtLongTime.getSubject().equals(claims.getSubject())) {
				String message = "Wrong JWT permanent token found for this token : %1$s";
				logger.warn(String.format(message, claims.get("uuid")));
				jwtAuthentication.setSubject(claims.get("sub", String.class));
				throw new JwtBadFormatException(String.format(message, token));
			}
			if (!authentificationFacade.isJwtLongTimeFunctionalityEnabled(jwtLongTime.getDomain().getUuid())) {
				throw new AuthenticationServiceException("JWT permanent token Functionality is disabled.");
			}
		} else if (!jwtService.hasValidLiveTime(claims)) {
			String msg = "Token live time can not be more than 5 minutes for jwt token: %1$s";
			// do not leak token in logs, too dangerous
			logger.warn(msg);
			throw new AuthenticationServiceException(String.format(msg, token));
		}
		logger.debug("JWT token seems to be good. Processing authentication...");

		User foundUser = null;
		String email = claims.getSubject();
		String domainUuid = claims.get("domain", String.class);
		List<String> domains = Lists.newArrayList();
		if (domainUuid == null) {
			foundUser = authentificationFacade.findByLogin(email);
			domains = authentificationFacade.getAllDomains();
		} else {
			foundUser = authentificationFacade.findByDomainAndMail(domainUuid, email);
			domains = authentificationFacade.getAllSubDomainIdentifiers(domainUuid);
		}
		if (foundUser == null) {
			for (String domain : domains) {
				foundUser = authentificationFacade.userProviderSearchForAuth(domain, email);
				if (foundUser != null) {
					break;
				}
			}
		}
		if (foundUser == null) {
			logger.error("User not found: " + claims);
			throw new UsernameNotFoundException("Could not find user account : " + claims);
		}
		try {
			// loading/creating the real entity
			foundUser = authentificationFacade.findOrCreateUser(foundUser.getDomainId(), foundUser.getMail());
		} catch (BusinessException e) {
			logger.error(e);
			throw new AuthenticationServiceException(
					"Could not create user account : "
					+ foundUser.getDomainId() + " : "
					+ foundUser.getMail(), e);
		}

		logger.info(String.format("Successful authentication of  %1$s with JWT token : %2$s", foundUser.getLsUuid(), claims));
		List<GrantedAuthority> grantedAuthorities = RoleProvider.getRoles(foundUser);
		UserDetails userDetail = new org.springframework.security.core.userdetails.User(foundUser.getLsUuid(), "", true,
				true, true, true, grantedAuthorities);

		return new UsernamePasswordAuthenticationToken(userDetail, jwtAuthentication.getCredentials(), grantedAuthorities);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (JwtAuthenticationToken.class).isAssignableFrom(authentication);
	}

}
