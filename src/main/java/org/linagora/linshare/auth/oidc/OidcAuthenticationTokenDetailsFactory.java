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

import java.util.List;
import java.util.Optional;

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
		User foundUser;
		String email = claims.getEmail();
		try {
			Validate.notEmpty(email, "Missing email claim.");
			Optional<List<String>> domainDiscriminator = Optional.ofNullable(List.of(claims.getDomainDiscriminator()));
			String externalUid = claims.getExternalUid();
			if (domainDiscriminator.isPresent()) {
				OIDCUserProviderDto providerDto = authentificationFacade.findOidcProvider(domainDiscriminator.get());
				if (providerDto.getCheckExternalUserID() ) {
					Validate.notEmpty(externalUid, "Missing external_uid claim.");
				}
				if (providerDto.getUseAccessClaim() ) {
					String allowed = claims.getLinshareAccess();
					Validate.notEmpty(allowed, "Missing linshare_access claim.");
					if (!allowed.toLowerCase().equals(linshareAccessClaimValue)) {
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
				// looking in the db first.
				foundUser = authentificationFacade.findByLoginAndDomain(providerDto.getDomain().getUuid(), email);
				if (foundUser == null) {
					// looking through user provider.
					foundUser = authentificationFacade.ldapSearchForAuth(providerDto.getDomain().getUuid(), email);
				}
				if (foundUser == null) {
					logger.error("User not found: " + email);
					throw new UsernameNotFoundException("Could not find user account : " + email);
				}
				try {
					// It means we are using a OIDC user provider, so we need to provide some extra properties to allow on-the-fly creation.
					// It won't be use if the profile already exists.
					// loading/creating the real entity
					foundUser = authentificationFacade.findOrCreateUser(foundUser.getDomainId(), foundUser.getMail());
					if (!foundUser.getLdapUid().equals(externalUid)) {
						if (providerDto.getCheckExternalUserID() ) {
							logger.error("External uid has changed, same email but probably a different user. external_uid={}, {}", externalUid, foundUser);
							throw new UsernameNotFoundException("Access rejected, external uid does not match with existing profile");
						} else {
							logger.debug("External uid has changed, same email but probably a different user. {}, {}", externalUid, foundUser);
						}
					}
				} catch (BusinessException e2) {
					logger.error(e2.getMessage(), e2);
					throw new AuthenticationServiceException(
							"Could not create user account : "
							+ foundUser.getDomainId() + " : "
							+ foundUser.getMail(), e2);
				}
			} else {
				foundUser = authentificationFacade.findByLogin(email);
				if (foundUser == null) {
					// looking through user providers.
					List<String> domains = authentificationFacade.getAllDomains();
					for (String domain : domains) {
						logger.trace("searching into domain: {}", domain);
						foundUser = authentificationFacade.ldapSearchForAuth(domain, email);
						if (foundUser != null) {
							break;
						}
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
			}
		} catch (NullPointerException|IllegalArgumentException e) {
			logger.error(e.getMessage(), e);
			throw new LinShareAuthenticationException("Missing some claim values: " + e.getMessage()) {
						private static final long serialVersionUID = -2805671638935042756L;
						@Override
						public LinShareAuthenticationExceptionCode getErrorCode() {
							return LinShareAuthenticationExceptionCode.DOMAIN_NOT_FOUND;
						}
					};
		}
		logger.info(String.format("Successful authentication of  %1$s with OIDC opaque token : %2$s", foundUser.getLsUuid(), email));
		List<GrantedAuthority> grantedAuthorities = RoleProvider.getRoles(foundUser);
		grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_AUTH_OIDC));
		UserDetails userDetail = new org.springframework.security.core.userdetails.User(foundUser.getLsUuid(), "", true,
				true, true, true, grantedAuthorities);

		return new UsernamePasswordAuthenticationToken(userDetail, null, grantedAuthorities);
	}
}
