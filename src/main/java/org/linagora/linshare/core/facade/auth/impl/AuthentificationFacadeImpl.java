/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.facade.auth.impl;

import java.util.List;
import java.util.Optional;

import org.linagora.linshare.auth.exceptions.LinShareAuthenticationException;
import org.linagora.linshare.auth.exceptions.LinShareAuthenticationExceptionCode;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.OIDCUserProvider;
import org.linagora.linshare.core.domain.entities.User;
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
import org.linagora.linshare.mongo.entities.logs.AuthenticationAuditLogEntryUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthentificationFacadeImpl implements AuthentificationFacade {

	private static final Logger logger = LoggerFactory.getLogger(AuthentificationFacadeImpl.class);

	private final UserService userService;

	private final LogEntryService logEntryService;

	private final AbstractDomainService abstractDomainService;

	private final UserProviderService userProviderService;

	private final UserRepository<User> userRepository;

	private final OIDCUserProviderRepository oidcUserProviderRepository;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	public AuthentificationFacadeImpl(UserService userService, LogEntryService logEntryService,
			AbstractDomainService abstractDomainService, UserProviderService userProviderService,
			FunctionalityReadOnlyService functionalityReadOnlyService,
			OIDCUserProviderRepository oidcUserProviderRepository,
			UserRepository<User> userRepository) {
		super();
		this.userService = userService;
		this.logEntryService = logEntryService;
		this.abstractDomainService = abstractDomainService;
		this.userProviderService = userProviderService;
		this.userRepository = userRepository;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.oidcUserProviderRepository = oidcUserProviderRepository;
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
	public User ldapSearchForAuth(String domainIdentifier, String login)
			throws BusinessException {
		AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
		User user = userProviderService.searchForAuth(domain, domain.getUserProvider(), login);
		return user;
	}

	@Override
	public User findByLogin(String login) {
		User user = userRepository.findByLogin(login);
		// Ugly but needed until we find a more elegant solution :(
		if (user != null) user.getDomain().getUuid();
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
		if(ldapSearchForAuth(
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
	public OIDCUserProviderDto findOidcProvider(String domainDiscriminator) {
		logger.debug("looking for domain with discriminator: {}", domainDiscriminator);
		Optional<OIDCUserProvider> userProvider = oidcUserProviderRepository.findByDomainDiscriminator(domainDiscriminator);
		if (userProvider.isEmpty()) {
			String msg = "Can not find domain using domain discriminator: " + domainDiscriminator;
			logger.error(msg);
			throw new LinShareAuthenticationException(msg) {

				private static final long serialVersionUID = -2805671638935042756L;

				@Override
				public LinShareAuthenticationExceptionCode getErrorCode() {
					return LinShareAuthenticationExceptionCode.DOMAIN_NOT_FOUND;
				}
			};
		}
		return new OIDCUserProviderDto(userProvider.get());
	}
}