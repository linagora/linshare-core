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
package org.linagora.linshare.core.facade.auth.impl;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.linagora.linshare.auth.exceptions.LinShareAuthenticationException;
import org.linagora.linshare.auth.exceptions.LinShareAuthenticationExceptionCode;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.OIDCUserProvider;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.auth.AuthentificationFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.OIDCUserProviderDto;
import org.linagora.linshare.core.repository.OIDCUserProviderRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.UserProviderService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.mongo.entities.logs.AuthenticationAuditLogEntryUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class AuthentificationFacadeImpl implements AuthentificationFacade {

	private static final Logger logger = LoggerFactory.getLogger(AuthentificationFacadeImpl.class);

	private final UserService userService;

	private final LogEntryService logEntryService;

	private final AbstractDomainService abstractDomainService;

	private final UserProviderService userProviderService;

	private final UserRepository<User> userRepository;

	private final OIDCUserProviderRepository oidcUserProviderRepository;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final GuestService guestService;

	public AuthentificationFacadeImpl(UserService userService, LogEntryService logEntryService,
			AbstractDomainService abstractDomainService, UserProviderService userProviderService,
			FunctionalityReadOnlyService functionalityReadOnlyService,
			OIDCUserProviderRepository oidcUserProviderRepository,
			UserRepository<User> userRepository,
			final GuestService guestService) {
		super();
		this.userService = userService;
		this.logEntryService = logEntryService;
		this.abstractDomainService = abstractDomainService;
		this.userProviderService = userProviderService;
		this.userRepository = userRepository;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.oidcUserProviderRepository = oidcUserProviderRepository;
		this.guestService = guestService;
	}

	@Override
	public User loadUserDetails(String uuid) throws BusinessException {
		return userService.findByLsUuid(uuid);
	}

	@Override
	public User findOrCreateUser(String domainIdentifier, String mail)
			throws BusinessException {
		return userService.findOrCreateUser(mail, domainIdentifier);
	}

	@Override
	public User findOrCreateUserByExternalUid(String domainIdentifier, @NotNull String externalUid)
			throws BusinessException {
		return userService.findOrCreateUserByExternalUid(externalUid, domainIdentifier);
	}

	@Override
	public void logAuthError(String login, String domainIdentifier, String message) throws BusinessException {
		AuthenticationAuditLogEntryUser log = new AuthenticationAuditLogEntryUser(login, domainIdentifier,
				LogAction.FAILURE, AuditLogEntryType.AUTHENTICATION, message);
		logEntryService.insert(log);
	}

	@Override
	public boolean userExist(String lsUuid) {
		return userService.exist(lsUuid);
	}

	@Override
	public void logAuthError(String userUuid, String message)
			throws BusinessException {
		User user = userService.updateUserForFailureAuthentication(userUuid);
		AuthenticationAuditLogEntryUser log = new AuthenticationAuditLogEntryUser(user, LogAction.FAILURE, AuditLogEntryType.AUTHENTICATION, message);
		logEntryService.insert(log);
	}

	@Override
	public void logAuthError(User user, String domainIdentifier, String message)
			throws BusinessException {
		// Reloading entity inside a new transaction/session.
		user = userService.findByLsUuid(user.getLsUuid());
		AuthenticationAuditLogEntryUser log = new AuthenticationAuditLogEntryUser(user, LogAction.FAILURE, AuditLogEntryType.AUTHENTICATION, message);
		logEntryService.insert(log);
	}

	@Override
	public void logAuthSuccess(String userUuid) throws BusinessException {
		// Reloading entity inside a new transaction/session.
		User user = userService.updateUserForSuccessfulAuthentication(userUuid);
		AuthenticationAuditLogEntryUser log = new AuthenticationAuditLogEntryUser(user, LogAction.SUCCESS, AuditLogEntryType.AUTHENTICATION, "Successfull authentification");
		logEntryService.insert(log);
	}

	@Override
	public AbstractDomain retrieveDomain(String domainIdentifier) {
		return abstractDomainService.retrieveDomain(domainIdentifier);
	}

	@Override
	public boolean isExist(String domainUuid) {
		AbstractDomain domain = abstractDomainService.retrieveDomain(domainUuid);
		if (domain != null) {
			return true;
		}
		return false;
	}

	@Override
	public List<String> getAllSubDomainIdentifiers(String domainIdentifier) {
		return abstractDomainService.getAllSubDomainIdentifiers(domainIdentifier);
	}

	@Override
	public List<String> getAllDomains() {
		return abstractDomainService.findAllDomainIdentifiersForAuthenticationDiscovery();
	}

	@Override
	public User ldapAuth(String domainIdentifier, String login,
			String userPasswd) throws BusinessException {
		AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
		return userProviderService.auth(domain.getUserProvider(), login, userPasswd);
	}

	@Override
	public User userProviderSearchForAuth(String domainIdentifier, String login)
			throws BusinessException {
		AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
		User user = userProviderService.searchForAuth(domain, domain.getUserProvider(), login);
		return user;
	}

	@Override
	public User findByLogin(String login) {
		User user = userRepository.findByLogin(login);
		// Ugly but needed until we find a more elegant solution :(
		if (user != null) {
			user.getDomain().getUuid();
		}
		return user;
	}

	@Override
	public User findByExternalUid(@NotNull String externalUid) {
		User user = userRepository.findByExternalUid(externalUid);
		// Ugly but needed until we find a more elegant solution :(
		if (user != null) {
			user.getDomain().getUuid();
		}
		return user;
	}

	@Override
	public User findByLoginAndDomain(String domain, String login) {
		User internal = userRepository.findByLoginAndDomain(domain, login);
		// Ugly but needed until we find a more elegant solution :(
		if (internal != null) internal.getDomain().getUuid();
		return internal;
	}

	private User updateUser(User user) throws BusinessException {
		Account system = userRepository.getBatchSystemAccount();
		return userService.updateUser(system, user, user.getDomainId());
	}

	@Override
	public User checkStillInLdap(User user, String login) throws BusinessException {
		logger.debug("User found in DB : "
				+ user.getAccountRepresentation());
		logger.debug("The user domain stored in DB was : "
				+ user.getDomainId());
		if(userProviderSearchForAuth(
				user.getDomainId(), login) == null) {
			// The previous user found into the database does not exists anymore into the LDAP directory.
			// We must not use him.
			logger.warn("authentication process : the current user does not exist anymore into the LDAP directory : " + user.getAccountRepresentation());
			// So week flag him as inconsistent.
			user.setInconsistent(true);
			updateUser(user);
			user = null;
		} else {
			if (user.isInconsistent()) {
				//The user was found and no longer inconsistent so we unflag him.
				user.setInconsistent(false);
				updateUser(user);
			}
		}
		return user;
	}

	@Override
	public boolean isJwtLongTimeFunctionalityEnabled(String domainUuid) {
		Functionality functionality = functionalityReadOnlyService.getJwtLongTimeFunctionality(domainUuid);
		if (!functionality.getActivationPolicy().getStatus()) {
			return false;
		}
		return true;
	}

	@Override
	public OIDCUserProviderDto findOidcProvider(List<String> domainDiscriminators) {
		logger.debug("looking for domain with discriminator: {}", domainDiscriminators);
		List<OIDCUserProvider> userProvider = oidcUserProviderRepository.findAllByDomainDiscriminator(domainDiscriminators);
		if (userProvider.isEmpty()) {
			String msg = "Can not find domain using domain discriminators: " + domainDiscriminators;
			logger.error(msg);
			throw new LinShareAuthenticationException(msg) {
				private static final long serialVersionUID = -2805671638935042756L;
				@Override
				public LinShareAuthenticationExceptionCode getErrorCode() {
					return LinShareAuthenticationExceptionCode.DOMAIN_NOT_FOUND;
				}
			};
		}
		if (userProvider.size() >= 2) {
			String msg = "User should not belong to mutiple domains (discriminators): " + domainDiscriminators;
			logger.error(msg);
			throw new LinShareAuthenticationException(msg) {
				private static final long serialVersionUID = 4157262685317509985L;
				@Override
				public LinShareAuthenticationExceptionCode getErrorCode() {
					return LinShareAuthenticationExceptionCode.MULTIPLE_DOMAIN_NOT_FOUND;
				}
			};
		}
		return new OIDCUserProviderDto(userProvider.get(0));
	}

	@Override
	public void convertGuestToInternalUser(@Nonnull final SystemAccount systemAccount,
										   @Nonnull final Account authUser,
										   @Nonnull final User guestUser){
		this.guestService.convertGuestToInternalUser(systemAccount, authUser, guestUser);
	}

	@Override
	public void deleteUser(@Nonnull final SystemAccount systemAccount, @Nonnull final String uuid) throws BusinessException {
		this.guestService.deleteUser(systemAccount, uuid);
	}
}