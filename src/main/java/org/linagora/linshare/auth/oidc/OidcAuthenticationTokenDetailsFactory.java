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
package org.linagora.linshare.auth.oidc;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.linagora.linshare.auth.AuthRole;
import org.linagora.linshare.auth.RoleProvider;
import org.linagora.linshare.auth.exceptions.LinShareAuthenticationException;
import org.linagora.linshare.auth.exceptions.LinShareAuthenticationExceptionCode;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.auth.AuthentificationFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.OIDCUserProviderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Iterator;
import java.util.List;

/**
 * Loading data from the LinShare database by the information from the oAUth2 provider.
 */
public class OidcAuthenticationTokenDetailsFactory {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private AuthentificationFacade authentificationFacade;

	private String linshareAccessClaimValue;

	public OidcAuthenticationTokenDetailsFactory(AuthentificationFacade authentificationFacade,
												 String linshareAccessClaimValue) {
		this.authentificationFacade  = authentificationFacade;
		this.linshareAccessClaimValue = linshareAccessClaimValue;
	}

	@NotNull
	UsernamePasswordAuthenticationToken getAuthenticationToken(OidcLinShareUserClaims claims) {
		Validate.notNull(claims, "Missing user claims.");

		User foundUser = getUserFromClaims(claims);
		Validate.notNull(foundUser, "User should have been set or created by now.");
		logger.info(String.format("Successful authentication of  %1$s with OIDC opaque token : %2$s", foundUser.getLsUuid(), claims.getEmail()));

		return buildAuthenticationToken(foundUser);
	}

	@NotNull
	private static UsernamePasswordAuthenticationToken buildAuthenticationToken(User foundUser) {
		List<GrantedAuthority> grantedAuthorities = RoleProvider.getRoles(foundUser);
		grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_AUTH_OIDC));
		UserDetails userDetail = new org.springframework.security.core.userdetails.User(foundUser.getLsUuid(), "", true,
				true, true, true, grantedAuthorities);
		return new UsernamePasswordAuthenticationToken(userDetail, null, grantedAuthorities);
	}

	private User getUserFromClaims(OidcLinShareUserClaims claims) {
		try {
			Validate.notEmpty(claims.getExternalUid(), "Missing external_uid claim.");
			return getUserFromExternalUid(claims.getExternalUid());
		} catch (NullPointerException | IllegalArgumentException e) {
			throwMissingClaimException(e);
		}
		return null;
	}


	private User getUserFromMail(String email) {
		User foundUser = authentificationFacade.findByLogin(email);
		if (foundUser == null) {
			// looking through user providers.
			Iterator<String> domains = authentificationFacade.getAllDomains().iterator();
			while (domains.hasNext() && foundUser == null) {
				String domain = domains.next();
				logger.trace("searching into domain: {}", domain);
				foundUser = authentificationFacade.userProviderSearchForAuth(domain, email);
			}
		}
		if (foundUser == null) {
			logger.error("User not found: " + email);
			throw new UsernameNotFoundException("Could not find user account : " + email);
		}
		try {
			// loading/creating the real entity
			foundUser = authentificationFacade.findOrCreateUser(foundUser.getDomainId(), foundUser.getMail());
		} catch (BusinessException e) {
			logger.error(e.getMessage(), e);
			throw new AuthenticationServiceException(
					"Could not create user account : "
					+ foundUser.getDomainId() + " : "
					+ foundUser.getMail(), e);
		}
		return foundUser;
	}

	private User getUserFromExternalUid(@NotNull String externalUid) {
		User foundUser = authentificationFacade.findByExternalUid(externalUid);
		if (foundUser == null) {
			// looking through user providers.
			Iterator<String> domains = authentificationFacade.getAllDomains().iterator();
			while (domains.hasNext() && foundUser == null) {
				String domain = domains.next();
				logger.trace("searching into domain: {}", domain);
				foundUser = authentificationFacade.userProviderSearchForAuth(domain, externalUid);
			}
		}
		if (foundUser == null) {
			logger.error("User not found: " + externalUid);
			throw new UsernameNotFoundException("Could not find user account : " + externalUid);
		}
		try {
			// loading/creating the real entity
			foundUser = authentificationFacade.findOrCreateUserByExternalUid(foundUser.getDomainId(), externalUid);
		} catch (BusinessException e) {
			logger.error(e.getMessage(), e);
			throw new AuthenticationServiceException(
					"Could not create user account : "
							+ foundUser.getDomainId() + " : "
							+ foundUser.getMail(), e);
		}
		return foundUser;
	}

	@NotNull
	private User getUserFromDomainProvider(OidcLinShareUserClaims claims) {
		OIDCUserProviderDto providerDto = authentificationFacade.findOidcProvider(List.of(claims.getDomainDiscriminator()));
		User validatedUser = validateUserFromDbOrUserProvider(claims, providerDto);
		return createOrUpdateUser(claims, providerDto, validatedUser);
	}

	@NotNull
	private User createOrUpdateUser(OidcLinShareUserClaims claims, OIDCUserProviderDto providerDto, User validatedUser) {
		try {
			// It means we are using a OIDC user provider, so we need to provide some extra properties to allow on-the-fly creation.
			// It won't be used if the profile already exists.
			// If we use an opaque token we should transmit it to extract user data later
			// loading/creating the real entity
			User foundUser = authentificationFacade.findOrCreateUserByExternalUid(validatedUser.getDomainId(), claims.getExternalUid());
			String externalUid = claims.getExternalUid();
			if (!foundUser.getLdapUid().equals(externalUid)) {
				if (providerDto.getCheckExternalUserID() ) {
					logger.error("External uid has changed, same email but probably a different user. external_uid={}, {}", externalUid, foundUser);
					throw new UsernameNotFoundException("Access rejected, external uid does not match with existing profile");
				} else {
					logger.debug("External uid has changed, same email but probably a different user. {}, {}", externalUid, foundUser);
				}
			}
			return foundUser;
		} catch (BusinessException e) {
			logger.error(e.getMessage(), e);
			throw new AuthenticationServiceException(
					"Could not create user account : "
					+ validatedUser.getDomainId() + " : "
					+ validatedUser.getMail(), e);
		}
	}

	@NotNull
	private User validateUserFromDbOrUserProvider(OidcLinShareUserClaims claims, OIDCUserProviderDto providerDto) {
		String email = claims.getEmail();
		Validate.notEmpty(email, "Missing email claim.");

		String identifier = email;

		// Optional validation
		if (providerDto.getCheckExternalUserID() ) {
			identifier = claims.getExternalUid();
			Validate.notEmpty(identifier, "Missing external_uid claim.");
		}

		if (providerDto.getUseAccessClaim() ) {
			String allowed = claims.getLinshareAccess();
			Validate.notEmpty(allowed, "Missing linshare_access claim.");
			if (!allowed.toLowerCase().equals(linshareAccessClaimValue)) {
				throwAccessNotGrantedException(identifier);
			}
		}
		// looking in the db first.
		User foundUser;
		foundUser = authentificationFacade.findByLoginAndDomain(providerDto.getDomain().getUuid(), email);
		if (foundUser == null) {
			// looking through user provider.
			foundUser = authentificationFacade.userProviderSearchForAuth(providerDto.getDomain().getUuid(), identifier);
		}
		if (foundUser == null) {
			logger.error("User not found: " + identifier);
			throw new UsernameNotFoundException("Could not find user account : " + identifier);
		}
		return foundUser;
	}

	private void throwMissingClaimException(RuntimeException e) {
		logger.error(e.getMessage(), e);
		throw new LinShareAuthenticationException("Missing some claim values: " + e.getMessage()) {
			private static final long serialVersionUID = -2805671638935042756L;

			@Override
			public LinShareAuthenticationExceptionCode getErrorCode() {
				return LinShareAuthenticationExceptionCode.DOMAIN_NOT_FOUND;
			}
		};
	}

	private void throwAccessNotGrantedException(String email) {
		String msg = "Unauthorized access attempt : " + email;
		logger.warn(msg);
		throw new LinShareAuthenticationException(msg) {
			private static final long serialVersionUID = 3890006776875100561L;
			@Override
			public LinShareAuthenticationExceptionCode getErrorCode() {
				return LinShareAuthenticationExceptionCode.ACCESS_NOT_GRANTED;
			}
		};
	}
}
