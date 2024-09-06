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

import java.util.List;

import org.apache.commons.lang3.Validate;
import javax.annotation.Nonnull;
import org.linagora.linshare.auth.AuthRole;
import org.linagora.linshare.auth.RoleProvider;
import org.linagora.linshare.auth.exceptions.LinShareAuthenticationException;
import org.linagora.linshare.auth.exceptions.LinShareAuthenticationExceptionCode;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.auth.AuthentificationFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainLightDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.OIDCUserProviderDto;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Loading data from the LinShare database by the information from the oAUth2 provider.
 */
public class OidcAuthenticationTokenDetailsFactory {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private AuthentificationFacade authentificationFacade;

	private String linshareAccessClaimValue;
	private final GuestRepository guestRepository;
	private final boolean enableGuestConversion;
	protected final AccountRepository<Account> accountRepository;
	private final UserService userService;

	public OidcAuthenticationTokenDetailsFactory(final AuthentificationFacade authentificationFacade,
												 final String linshareAccessClaimValue,
												 final GuestRepository guestRepository,
												 final AccountRepository<Account> accountRepository,
												 final UserService userService,
												 final boolean enableGuestConversion) {
		this.authentificationFacade = authentificationFacade;
		this.linshareAccessClaimValue = linshareAccessClaimValue;
		this.guestRepository = guestRepository;
		this.accountRepository = accountRepository;
		this.userService = userService;
		this.enableGuestConversion = enableGuestConversion;
	}

	@Nonnull
	UsernamePasswordAuthenticationToken getAuthenticationToken(final OidcLinShareUserClaims claims) {
		Validate.notNull(claims, "Missing user claims.");
		final SystemAccount actor = getSystemAccount();
		final User foundUser = getUserFromDomainProvider(claims);
		Validate.notNull(foundUser, "User should have been set or created by now.");
		logger.info(String.format("Successful authentication of %1$s with OIDC token : %2$s", foundUser.getLsUuid(), claims.getEmail()));
		logger.debug("Checking if guest conversion is enabled");

		if (enableGuestConversion) {
			final Guest guestAccount = guestRepository.findByMail(foundUser.getMail());
			final Internal internalUser = userService.findInternalUserWithEmail(actor, foundUser.getMail());

			if (guestAccount != null && internalUser != null) {
				logger.debug("Guest user found: {} with account type: {}", guestAccount.getMail(), guestAccount.getAccountType());
				authentificationFacade.convertGuestToInternalUser(actor, foundUser, guestAccount);
				logger.info("Guest converted to internal user: {}", foundUser.getMail());
				logger.debug("Deleting the guest");
				authentificationFacade.deleteUser(actor, guestAccount.getLsUuid());
			} else {
				if (guestAccount == null) {
					logger.debug("No guest account found for user: {}", foundUser.getMail());
				}
				if (internalUser == null) {
					logger.debug("No internal user found for user: {}", foundUser.getMail());
				}
			}
		} else {
			logger.info("Guest conversion is not enabled. Cannot convert guest user: {}", foundUser.getMail());
		}

		return buildAuthenticationToken(foundUser);
	}

	protected SystemAccount getSystemAccount() {
		return accountRepository.getBatchSystemAccount();
	}

	@Nonnull
	private static UsernamePasswordAuthenticationToken buildAuthenticationToken(User foundUser) {
		List<GrantedAuthority> grantedAuthorities = RoleProvider.getRoles(foundUser);
		grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_AUTH_OIDC));
		UserDetails userDetail = new org.springframework.security.core.userdetails.User(foundUser.getLsUuid(), "", true,
				true, true, true, grantedAuthorities);
		return new UsernamePasswordAuthenticationToken(userDetail, null, grantedAuthorities);
	}

	@Nonnull
	private User getUserFromDomainProvider(OidcLinShareUserClaims claims) {
		OIDCUserProviderDto providerDto = authentificationFacade.findOidcProvider(List.of(claims.getDomainDiscriminator()));
		User validatedUser = validateUserFromDbOrUserProvider(claims, providerDto);
		return createOrUpdateUser(claims, providerDto, validatedUser);
	}

	@Nonnull
	private User createOrUpdateUser(OidcLinShareUserClaims claims, OIDCUserProviderDto providerDto, User validatedUser) {
		try {
			// It means we are using a OIDC user provider, so we need to provide some extra properties to allow on-the-fly creation.
			// It won't be used if the profile already exists.
			// If we use an opaque token we should transmit it to extract user data later
			// loading/creating the real entity
			final String externalUid = claims.getExternalUid();
			assert externalUid != null;
			final User foundUser;
			if (providerDto.getCheckExternalUserID()) {
				foundUser = authentificationFacade.findOrCreateUserByExternalUid(validatedUser.getDomainId(),
						claims.getExternalUid());
				if (!foundUser.getLdapUid().equals(externalUid)) {
					logger.error("External uid has changed, same email but probably a different user. external_uid={}, {}", externalUid, foundUser);
					throw new UsernameNotFoundException("Access rejected, external uid does not match with existing profile");
				}
			} else {
				foundUser = authentificationFacade.findOrCreateUser(validatedUser.getDomainId(), claims.getEmail());
				if (!foundUser.getLdapUid().equals(externalUid)) {
					logger.debug("External uid has changed, same email but probably a different user. {}, {}", externalUid, foundUser);
				}
			}
			return foundUser;
		} catch (BusinessException e) {
			logger.error(e.getMessage(), e);
			throw new AuthenticationServiceException(
					"Could not create user account : " + validatedUser.getDomainId() + " : " + validatedUser.getMail(),
					e);
		}
	}


	@Nonnull
	private User validateUserFromDbOrUserProvider(OidcLinShareUserClaims claims, OIDCUserProviderDto providerDto) {
		final boolean useExternalUid = providerDto.getCheckExternalUserID();
		String email = claims.getEmail();
		Validate.notEmpty(email, "Missing email claim.");

		String identifier = email;

		// Optional validation
		if (useExternalUid) {
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

		DomainLightDto domain = providerDto.getDomain();
		Validate.notNull(domain, "Missing domain for user.");
		Validate.notEmpty(domain.getUuid(), "Missing domain uuid for user.");

		// looking in the db first.
		User foundUser;
		if (useExternalUid) {
			foundUser = authentificationFacade.findByDomainAndMail(providerDto.getDomain().getUuid(), identifier);
		} else {
			foundUser = authentificationFacade.findByDomainAndExternalUid(providerDto.getDomain().getUuid(),
					identifier);
		}

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
