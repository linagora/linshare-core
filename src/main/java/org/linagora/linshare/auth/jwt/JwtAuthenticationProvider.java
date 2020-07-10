/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
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
package org.linagora.linshare.auth.jwt;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linagora.linshare.auth.RoleProvider;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.auth.AuthentificationFacade;
import org.linagora.linshare.core.repository.UserRepository;
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
import io.jsonwebtoken.SignatureException;

public class JwtAuthenticationProvider implements AuthenticationProvider {

	private final static Log logger = LogFactory.getLog(JwtAuthenticationProvider.class);

	private AuthentificationFacade authentificationFacade;

	private JwtService jwtService;

	private JwtLongTimeMongoRepository jwtLongTimeMongoRepository;

	private UserRepository<User> userRepository;

	public void setAuthentificationFacade(AuthentificationFacade authentificationFacade) {
		this.authentificationFacade = authentificationFacade;
	}

	public void setJwtService(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	public void setJwtLongTimeMongoRepository(JwtLongTimeMongoRepository jwtLongTimeMongoRepository) {
		this.jwtLongTimeMongoRepository = jwtLongTimeMongoRepository;
	}

	public void setUserRepository(UserRepository<User> userRepository) {
		this.userRepository = userRepository;
	}

	// TODO:JWT: log authentication attempts with failures
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
			// TODO:JWT: log authentication failure
//			authentificationFacade.logAuthError(user, message);
			throw new AuthenticationServiceException("The token is expired and not valid anymore : " + token, e);
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
				insertJwtLongTimeFailureLogEntry(claims, message);
				String msg = String.format(message, token);
				throw new AuthenticationServiceException(msg);
			} else if (!jwtLongTime.getSubject().equals(claims.getSubject())) {
				String message = "Wrong JWT permanent token found for this token : %1$s";
				insertJwtLongTimeFailureLogEntry(claims, message);
				String msg = String.format(message, token);
				throw new AuthenticationServiceException(msg);
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
			foundUser = authentificationFacade.findByLoginAndDomain(domainUuid, email);
			domains = authentificationFacade.getAllSubDomainIdentifiers(domainUuid);
		}
		if (foundUser == null) {
			for (String domain : domains) {
				foundUser = authentificationFacade.ldapSearchForAuth(domain, email);
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

		authentificationFacade.logAuthSuccess(foundUser.getLsUuid());
		logger.info(String.format("Successful authentication of  %1$s with JWT token : %2$s", foundUser.getLsUuid(), claims));
		List<GrantedAuthority> grantedAuthorities = RoleProvider.getRoles(foundUser);
		UserDetails userDetail = new org.springframework.security.core.userdetails.User(foundUser.getLsUuid(), "", true,
				true, true, true, grantedAuthorities);

		return new UsernamePasswordAuthenticationToken(userDetail, authentication.getCredentials(), grantedAuthorities);
	}

	private void insertJwtLongTimeFailureLogEntry(Claims claims, String message) {
		logger.warn(message);
		User found = userRepository.findByMail(claims.getSubject());
		String msg = new String();
		if (claims.containsKey("uuid") && claims.get("uuid") != null) {
			if (found != null) {
				msg = String.format(message, claims.get("uuid"));
				authentificationFacade.logAuthError(found, found.getDomainId(), msg);
			} else {
				msg = String.format("User not found for this JWT permanent token : %1$s", claims.get("uuid"));
				authentificationFacade.logAuthError(claims.getSubject(), null, msg);
			}
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (JwtAuthenticationToken.class).isAssignableFrom(authentication);
	}

}
