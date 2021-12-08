/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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
package org.linagora.linshare.core.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.naming.NamingException;

import org.apache.commons.lang3.Validate;
import org.hibernate.criterion.Order;
import org.linagora.linshare.auth.oidc.OidcOpaqueAuthenticationToken;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.constants.UserProviderType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.LdapUserProvider;
import org.linagora.linshare.core.domain.entities.OIDCUserProvider;
import org.linagora.linshare.core.domain.entities.TwakeGuestUserProvider;
import org.linagora.linshare.core.domain.entities.TwakeUserProvider;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserLdapPattern;
import org.linagora.linshare.core.domain.entities.UserProvider;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DomainPatternRepository;
import org.linagora.linshare.core.repository.LdapUserProviderRepository;
import org.linagora.linshare.core.repository.UserProviderRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.LDAPUserQueryService;
import org.linagora.linshare.core.service.UserProviderService;
import org.linagora.linshare.mongo.entities.logs.DomainPatternAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.DomainPatternMto;
import org.linagora.linshare.mongo.repository.AuditAdminMongoRepository;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.CommunicationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.collect.Lists;

public class UserProviderServiceImpl extends GenericAdminServiceImpl implements UserProviderService {

	private static final Logger logger = LoggerFactory
			.getLogger(UserProviderServiceImpl.class);

	private final DomainPatternRepository domainPatternRepository;

	private final LDAPUserQueryService ldapQueryService;

	private final UserProviderRepository userProviderRepository;

	private final LdapUserProviderRepository ldapUserProviderRepository;

	private final UserRepository<User> userRepository;

	private final AuditAdminMongoRepository mongoRepository;

	private final AbstractDomainRepository abstractDomainRepository;

	private final TwakeUserProviderServiceImpl twakeUserProviderService;

	private final TwakeGuestUserProviderServiceImpl twakeGuestUserProviderService;

	public UserProviderServiceImpl(
			DomainPatternRepository domainPatternRepository,
			LDAPUserQueryService ldapQueryService,
			LdapUserProviderRepository ldapUserProviderRepository,
			UserProviderRepository userProviderRepository,
			AuditAdminMongoRepository mongoRepository,
			UserRepository<User> userRepository,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			AbstractDomainRepository abstractDomainRepository,
			TwakeUserProviderServiceImpl twakeUserProviderService,
			TwakeGuestUserProviderServiceImpl twakeGuestUserProviderService) {
		super(sanitizerInputHtmlBusinessService);
		this.domainPatternRepository = domainPatternRepository;
		this.ldapQueryService = ldapQueryService;
		this.userProviderRepository = userProviderRepository;
		this.ldapUserProviderRepository = ldapUserProviderRepository;
		this.userRepository = userRepository;
		this.mongoRepository = mongoRepository;
		this.abstractDomainRepository = abstractDomainRepository;
		this.twakeUserProviderService = twakeUserProviderService;
		this.twakeGuestUserProviderService = twakeGuestUserProviderService;
	}

	@Override
	public UserLdapPattern createDomainPattern(Account actor, UserLdapPattern domainPattern)
			throws BusinessException {
		preChecks(actor);
		Validate.notEmpty(domainPattern.getLabel());
		Validate.notEmpty(domainPattern.getAuthCommand());
		Validate.notEmpty(domainPattern.getSearchUserCommand());
		Validate.notEmpty(domainPattern.getAutoCompleteCommandOnAllAttributes());
		Validate.notEmpty(domainPattern
				.getAutoCompleteCommandOnFirstAndLastName());
		Collection<LdapAttribute> collection = domainPattern.getAttributes()
				.values();
		for (LdapAttribute e : collection) {
			if (e.getAttribute() == null)
				throw new BusinessException(
						BusinessErrorCode.LDAP_ATTRIBUTE_CONTAINS_NULL,
						"Attribute must be not null");
		}
		domainPattern.setLabel(sanitize(domainPattern.getLabel()));
		domainPattern.setDescription(sanitize(domainPattern.getDescription()));
		UserLdapPattern createdDomain = domainPatternRepository
				.create(domainPattern);
		DomainPatternAuditLogEntry log = new DomainPatternAuditLogEntry(actor, actor.getDomainId(),
				LogAction.CREATE, AuditLogEntryType.DOMAIN_PATTERN, domainPattern);
		mongoRepository.insert(log);
		return createdDomain;
	}

	@Override
	public LdapUserProvider find(String uuid)
			throws BusinessException {
		LdapUserProvider provider = ldapUserProviderRepository.findByUuid(uuid);
		if (provider == null) {
			throw new BusinessException(
					BusinessErrorCode.USER_PROVIDER_NOT_FOUND,
					"LDAP User Provider identifier no found.");
		}
		return provider;
	}

	@Override
	public boolean exists(String uuid) {
		LdapUserProvider provider = ldapUserProviderRepository.findByUuid(uuid);
		return provider != null;
	}

	@Override
	public UserLdapPattern deletePattern(Account actor, String patternToDelete) throws BusinessException {
		preChecks(actor);
		UserLdapPattern pattern = findDomainPattern(patternToDelete);
		if (isUsed(pattern)) {
			throw new BusinessException(
					BusinessErrorCode.DOMAIN_PATTERN_STILL_IN_USE,
					"Cannot delete pattern because still used by domains");
		}
		if (pattern.getSystem()) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_PATTERN_CANNOT_BE_REMOVED,
					"System domain patterns cannot be removed");
		}
		domainPatternRepository.delete(pattern);
		DomainPatternAuditLogEntry log = new DomainPatternAuditLogEntry(actor, actor.getDomainId(),
				LogAction.DELETE, AuditLogEntryType.DOMAIN_PATTERN, pattern);
		mongoRepository.insert(log);
		return pattern;
	}

	public boolean isUsed(UserLdapPattern pattern) {
		return userProviderRepository.isUsed(pattern);
	}

	@Override
	public boolean canDeletePattern(String patternToDelete) {
		UserLdapPattern pattern = findDomainPattern(patternToDelete);
		return !isUsed(pattern);
	}

	@Override
	public List<UserLdapPattern> findAllDomainPattern() {
		return domainPatternRepository.findAll();
	}

	@Override
	public UserLdapPattern findDomainPattern(String uuid)
			throws BusinessException {
		Validate.notEmpty(uuid, "Domain pattern uuid must be set.");
		UserLdapPattern pattern = domainPatternRepository.findByUuid(uuid);
		if (pattern == null)
			throw new BusinessException(
					BusinessErrorCode.DOMAIN_PATTERN_NOT_FOUND,
					"Can not found domain pattern with identifier: " + uuid + ".");
		return pattern;
	}

	@Override
	public List<UserLdapPattern> findAllUserDomainPattern()
			throws BusinessException {
		return domainPatternRepository.findAllUserDomainPattern();
	}

	@Override
	public List<UserLdapPattern> findAllSystemDomainPattern()
			throws BusinessException {
		return domainPatternRepository.findAllSystemDomainPattern();
	}

	@Override
	public UserLdapPattern updateDomainPattern(Account actor, UserLdapPattern domainPattern)
			throws BusinessException {
		preChecks(actor);
		UserLdapPattern pattern = domainPatternRepository
				.findByUuid(domainPattern.getUuid());
		DomainPatternAuditLogEntry log = new DomainPatternAuditLogEntry(actor, actor.getDomainId(),
				LogAction.UPDATE, AuditLogEntryType.DOMAIN_PATTERN, pattern);
		if (pattern == null) {
			throw new BusinessException(
					BusinessErrorCode.DOMAIN_PATTERN_NOT_FOUND,
					"no such domain pattern");
		}
		if (pattern.getSystem()) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_PATTERN_CANNOT_BE_UPDATED,
					"System domain patterns cannot be updated");
		}
		Validate.notNull(domainPattern.getCompletionPageSize(), "Pattern's completion page size must be set.");
		Validate.notNull(domainPattern.getCompletionSizeLimit(), "Pattern's completion size limit must be set.");
		Validate.notNull(domainPattern.getSearchPageSize(), "Pattern's search page size must be set.");
		Validate.notNull(domainPattern.getSearchSizeLimit(), "Pattern's search page size must be set.");
		Validate.notEmpty(domainPattern.getAuthCommand(), "Pattern's auth command must be set.");
		Validate.notEmpty(domainPattern.getAutoCompleteCommandOnAllAttributes(), "Patterns's auto complete command on all attributes must be set.");
		Validate.notEmpty(domainPattern.getAutoCompleteCommandOnFirstAndLastName(), "Patterns's auto complete command on first name and last name must be set.");
		Validate.notEmpty(domainPattern.getSearchUserCommand(), "Patterns's search command user must be set.");
		pattern.setLabel(sanitize(domainPattern.getLabel()));
		pattern.setDescription(sanitize(domainPattern.getDescription()));
		pattern.setAuthCommand(domainPattern.getAuthCommand());
		pattern.setSearchUserCommand(domainPattern.getSearchUserCommand());
		pattern.setAutoCompleteCommandOnAllAttributes(domainPattern
				.getAutoCompleteCommandOnAllAttributes());
		pattern.setAutoCompleteCommandOnFirstAndLastName(domainPattern
				.getAutoCompleteCommandOnFirstAndLastName());
		pattern.setCompletionPageSize(domainPattern.getCompletionPageSize());
		pattern.setCompletionSizeLimit(domainPattern.getCompletionSizeLimit());
		pattern.setSearchPageSize(domainPattern.getSearchPageSize());
		pattern.setSearchSizeLimit(domainPattern.getSearchSizeLimit());
		pattern.getAttributes()
				.get(UserLdapPattern.USER_FIRST_NAME)
				.setAttribute(
						domainPattern.getAttributes()
								.get(UserLdapPattern.USER_FIRST_NAME)
								.getAttribute());
		pattern.getAttributes()
				.get(UserLdapPattern.USER_LAST_NAME)
				.setAttribute(
						domainPattern.getAttributes()
								.get(UserLdapPattern.USER_LAST_NAME)
								.getAttribute());
		pattern.getAttributes()
				.get(UserLdapPattern.USER_MAIL)
				.setAttribute(
						domainPattern.getAttributes()
								.get(UserLdapPattern.USER_MAIL).getAttribute());
		pattern.getAttributes()
				.get(UserLdapPattern.USER_UID)
				.setAttribute(
						domainPattern.getAttributes()
								.get(UserLdapPattern.USER_UID).getAttribute());
		pattern = domainPatternRepository.update(pattern);
		log.setResourceUpdated(new DomainPatternMto(pattern, true));
		mongoRepository.insert(log);
		return pattern;
	}

	@Override
	public LdapUserProvider create(LdapUserProvider userProvider) throws BusinessException {
		return ldapUserProviderRepository.create(userProvider);
	}

	@Override
	public void delete(UserProvider userProvider) throws BusinessException {
		userProviderRepository.delete(userProvider);
	}

	@Override
	public LdapUserProvider update(LdapUserProvider userProvider) throws BusinessException {
		return ldapUserProviderRepository.update(userProvider);
	}

	@Override
	public User findUser(AbstractDomain domain, UserProvider up, String mail)
			throws BusinessException {
		if (up != null) {
			if (UserProviderType.LDAP_PROVIDER.equals(up.getType())) {
				LdapUserProvider userProvider = ldapUserProviderRepository.load(up);
				User user = null;
				try {
					user = ldapQueryService.getUser(userProvider.getLdapConnection(),
							userProvider.getBaseDn(), userProvider.getPattern(), mail);
					if (user != null) {
						user.setDomain(domain);
						user.setRole(domain.getDefaultRole());
						user.setExternalMailLocale(user.getDomain().getExternalMailLocale());
					}
				} catch (NamingException | IOException | CommunicationException e) {
					throwError(userProvider.getLdapConnection(), e);
				}
				return user;
			} else if (UserProviderType.OIDC_PROVIDER.equals(up.getType())) {
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				if (!authentication.isAuthenticated()) {
					// it means we are trying to find/create this profile during authentication process.
					// This code is ugly, it is a quick workaround.
					if ((OidcOpaqueAuthenticationToken.class).isAssignableFrom(authentication.getClass())) {
						OidcOpaqueAuthenticationToken jwtAuthentication = (OidcOpaqueAuthenticationToken) authentication;
						OIDCUserProvider oidcUp = (OIDCUserProvider) up;
						String domainDiscriminator = jwtAuthentication.get("domain_discriminator");
						if (oidcUp.getDomainDiscriminator().equals(domainDiscriminator)) {
							String email = jwtAuthentication.get("email");
							Validate.notEmpty(email, "Missing required attribute for email.");
							String firstName = jwtAuthentication.get("first_name");
							Validate.notEmpty(firstName, "Missing required attribute for first name.");
							String lastName = jwtAuthentication.get("last_name");
							Validate.notEmpty(lastName, "Missing required attribute for last name.");
							Optional<String> externalUuid = Optional.ofNullable(jwtAuthentication.get("external_uid"));
							Internal internal = new Internal(firstName, lastName, email, externalUuid.orElse(email));
							internal.setDomain(domain);
							internal.setRole(domain.getDefaultRole());
							internal.setExternalMailLocale(domain.getExternalMailLocale());
							if (oidcUp.getUseRoleClaim()) {
								internal.setRole(Role.toDefaultRole(
									domain.getDefaultRole(),
									jwtAuthentication.get("linshare_role")));
							}
							if (oidcUp.getUseEmailLocaleClaim()) {
								internal.setExternalMailLocale(Language.toDefaultLanguage(
									domain.getExternalMailLocale(),
									jwtAuthentication.get("linshare_locale")));
							} else {
							}
							return internal;
						}
					}
				} else {
					// probably trying to discover a user.
					logger.debug("UserProviderType.OIDC provider does not supported discovering new user.");
				}
			} else if (UserProviderType.TWAKE_PROVIDER.equals(up.getType())) {
				return twakeUserProviderService.findUser(domain, (TwakeUserProvider) up, mail);
			} else if (UserProviderType.TWAKE_GUEST_PROVIDER.equals(up.getType())) {
				return twakeGuestUserProviderService.findUser(domain, (TwakeGuestUserProvider) up, mail);
			} else {
				logger.error("Unsupported UserProviderType : " + up.getType().toString() + ", id : " + up.getId());
			}
		}
		return null;
	}

	@Override
	public List<User> searchUser(AbstractDomain domain, UserProvider up,
			String mail, String firstName, String lastName) throws BusinessException {
		if (up != null) {
			if (UserProviderType.LDAP_PROVIDER.equals(up.getType())) {
				LdapUserProvider userProvider = ldapUserProviderRepository.load(up);
				List<User> users = new ArrayList<User>();
				try {
					users = ldapQueryService.searchUser(
							userProvider.getLdapConnection(), userProvider.getBaseDn(),
							userProvider.getPattern(), mail, firstName, lastName);
				} catch (NamingException  | IOException | CommunicationException e) {
					throwError(userProvider.getLdapConnection(), e);
				}
				return users;
			} else if (UserProviderType.OIDC_PROVIDER.equals(up.getType())) {
				PageContainer<User> container = new PageContainer<>(0,50);
				container = userRepository.findAll(domain, Order.asc("modificationDate"), mail, firstName,
						lastName, null, null, null, null, null, container);
				List<User> users = container.getPageResponse().getContent();
				return users;
			} else if (UserProviderType.TWAKE_PROVIDER.equals(up.getType())) {
				return twakeUserProviderService.searchUser(domain, (TwakeUserProvider) up, mail, firstName, lastName);
			} else if (UserProviderType.TWAKE_GUEST_PROVIDER.equals(up.getType())) {
				return twakeGuestUserProviderService.searchUser(domain, (TwakeGuestUserProvider) up, mail, firstName, lastName);
			} else {
				logger.error("Unsupported UserProviderType : " + up.getType().toString() + ", id : " + up.getId());
			}
		}
		return Lists.newArrayList();
	}

	@Override
	public List<User> autoCompleteUser(AbstractDomain domain,
			UserProvider up, String pattern) throws BusinessException {
		if (up != null) {
			if (UserProviderType.LDAP_PROVIDER.equals(up.getType())) {
				LdapUserProvider userProvider = ldapUserProviderRepository.load(up);
				List<User> users = new ArrayList<User>();
				try {
					users = ldapQueryService.completeUser(
							userProvider.getLdapConnection(), userProvider.getBaseDn(),
							userProvider.getPattern(), pattern);
				} catch (NamingException  | IOException | CommunicationException e) {
					logger.error(
							"Error while searching for a user with ldap connection {}",
							userProvider.getLdapConnection().getUuid());
					logger.error(e.getMessage());
					logger.debug(e.toString());
				}
				users.stream().map(user -> {user.setDomain(domain); return user;});
				return users;
			} else if (UserProviderType.OIDC_PROVIDER.equals(up.getType())) {
				PageContainer<User> container = new PageContainer<>(0,50);
				container = userRepository.findAll(domain, Order.asc("modificationDate"), pattern, null,
						null, null, null, null, null, null, container);
				List<User> users = container.getPageResponse().getContent();
				return users;
			} else if (UserProviderType.TWAKE_PROVIDER.equals(up.getType())) {
				return twakeUserProviderService.autoCompleteUser(domain, (TwakeUserProvider) up, pattern);
			} else if (UserProviderType.TWAKE_GUEST_PROVIDER.equals(up.getType())) {
				return twakeGuestUserProviderService.autoCompleteUser(domain, (TwakeGuestUserProvider) up, pattern);
			} else {
				logger.error("Unsupported UserProviderType : " + up.getType().toString() + ", id : " + up.getId());
			}
		}
		return Lists.newArrayList();
	}

	@Override
	public List<User> autoCompleteUser(AbstractDomain domain,
			UserProvider up, String firstName, String lastName) throws BusinessException {
		if (up != null) {
			if (UserProviderType.LDAP_PROVIDER.equals(up.getType())) {
				LdapUserProvider userProvider = ldapUserProviderRepository.load(up);
				List<User> users = new ArrayList<User>();
				try {
					users = ldapQueryService.completeUser(
							userProvider.getLdapConnection(), userProvider.getBaseDn(),
							userProvider.getPattern(), firstName, lastName);
				} catch (NamingException | IOException | CommunicationException e) {
					logger.error(
							"Error while searching for a user with ldap connection {}",
							userProvider.getLdapConnection().getUuid());
					logger.error(e.getMessage());
					logger.debug(e.toString());
				}
				users.stream().map(user -> {user.setDomain(domain); return user;});
				return users;
			} else if (UserProviderType.OIDC_PROVIDER.equals(up.getType())) {
				PageContainer<User> container = new PageContainer<>(0,50);
				container = userRepository.findAll(domain, Order.asc("modificationDate"), null, firstName,
						lastName, null, null, null, null, null,
						container);
				List<User> users = container.getPageResponse().getContent();
				return users;
			} else if (UserProviderType.TWAKE_PROVIDER.equals(up.getType())) {
				return twakeUserProviderService.autoCompleteUser(domain, (TwakeUserProvider) up, firstName, lastName);
			} else if (UserProviderType.TWAKE_GUEST_PROVIDER.equals(up.getType())) {
				return twakeGuestUserProviderService.autoCompleteUser(domain, (TwakeGuestUserProvider) up, firstName, lastName);
			} else {
				logger.error("Unsupported UserProviderType : " + up.getType().toString() + ", id : " + up.getId());
			}
		}
		return Lists.newArrayList();
	}

	@Override
	public Boolean isUserExist(AbstractDomain domain, UserProvider up, String mail)
			throws BusinessException {
		if (up != null) {
			if (UserProviderType.LDAP_PROVIDER.equals(up.getType())) {
				LdapUserProvider userProvider = ldapUserProviderRepository.load(up);
				Boolean result = false;
				try {
					result = ldapQueryService.isUserExist(
							userProvider.getLdapConnection(), userProvider.getBaseDn(),
							userProvider.getPattern(), mail);
				} catch (NamingException | IOException | CommunicationException e) {
					throwError(userProvider.getLdapConnection(), e);
				}
				return result;
			} else if (UserProviderType.OIDC_PROVIDER.equals(up.getType())) {
				User user = userRepository.findByMailAndDomain(domain.getUuid(), mail);
				if (user != null)
					return true;
			} else if (UserProviderType.TWAKE_PROVIDER.equals(up.getType())) {
				return twakeUserProviderService.isUserExist(domain, (TwakeUserProvider) up, mail);
			} else if (UserProviderType.TWAKE_GUEST_PROVIDER.equals(up.getType())) {
				return twakeGuestUserProviderService.isUserExist(domain, (TwakeGuestUserProvider) up, mail);
			} else {
				logger.error("Unsupported UserProviderType : " + up.getType().toString() + ", id : " + up.getId());
			}
		}
		return false;
	}

	@Override
	public User auth(UserProvider up, String login,
			String userPasswd) throws BusinessException {
		if (up != null) {
			if (UserProviderType.LDAP_PROVIDER.equals(up.getType())) {
				LdapUserProvider userProvider = ldapUserProviderRepository.load(up);
				User user = null;
				try {
					user = ldapQueryService.auth(userProvider.getLdapConnection(),
							userProvider.getBaseDn(),
							userProvider.getPattern(),
							login, userPasswd);
				} catch (NamingException | IOException | CommunicationException e) {
					throwError(userProvider.getLdapConnection(), e);
				}
				return user;
			} else if (UserProviderType.OIDC_PROVIDER.equals(up.getType())) {
				logger.debug("UserProviderType.OIDC does not provide an authentication through this method.");
			} else if (UserProviderType.TWAKE_PROVIDER.equals(up.getType())) {
				logger.debug("UserProviderType.Twake does not provide an authentication through this method.");
			} else if (UserProviderType.TWAKE_GUEST_PROVIDER.equals(up.getType())) {
				logger.debug("UserProviderType.TwakeGuest does not provide an authentication through this method.");
			} else {
				logger.error("Unsupported UserProviderType : " + up.getType().toString() + ", id : " + up.getId());
			}
		}
		return null;
	}

	@Override
	public User searchForAuth(AbstractDomain domain, UserProvider up, String login)
			throws BusinessException {
		if (up != null) {
			if (UserProviderType.LDAP_PROVIDER.equals(up.getType())) {
				LdapUserProvider userProvider = ldapUserProviderRepository.load(up);
				User user = null;
				try {
					user = ldapQueryService.searchForAuth(userProvider.getLdapConnection(),
							userProvider.getBaseDn(),
							userProvider.getPattern(), login);
				} catch (NamingException | IOException | CommunicationException e) {
					throwError(userProvider.getLdapConnection(), e);
				}
				if (user != null) {
					user.setDomain(domain);
				}
				return user;
			} else if (UserProviderType.OIDC_PROVIDER.equals(up.getType())) {
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				if(authentication !=null && !authentication.isAuthenticated()) {
					// It means we are trying to find this profile during authentication process.
					// It was the auto discover feature of LDAP, but it does not exist for OIDC.
					// This code is ugly workaround
					if ((OidcOpaqueAuthenticationToken.class).isAssignableFrom(authentication.getClass())){
						OidcOpaqueAuthenticationToken jwtAuthentication = (OidcOpaqueAuthenticationToken) authentication;
						OIDCUserProvider oidcUp = (OIDCUserProvider) up;
						String domainDiscriminator = jwtAuthentication.get("domain_discriminator");
						// if this user does not belong to this domain, we ignore him.
						if (oidcUp.getDomainDiscriminator().equals(domainDiscriminator)) {
							String email = jwtAuthentication.get("email");
							Validate.notEmpty(email, "Missing required attribute for email.");
							Internal internal = new Internal(null, null, email, null);
							internal.setDomain(domain);
							return internal;
						} else {
							logger.debug("Skipped. Provided oidcDomainIdentifier does not match current domain: {}, {}", domainDiscriminator, domain.getUuid());
						}
					}
				}
				logger.info("Using UserProviderType.OIDC provider outside authentication is not supported.");
			} else if (UserProviderType.TWAKE_PROVIDER.equals(up.getType())) {
				return twakeUserProviderService.searchForAuth(domain, (TwakeUserProvider) up, login);
			} else if (UserProviderType.TWAKE_GUEST_PROVIDER.equals(up.getType())) {
				return twakeGuestUserProviderService.searchForAuth(domain, (TwakeGuestUserProvider) up, login);
			} else {
				logger.error("Unsupported UserProviderType : " + up.getType().toString() + ", id : " + up.getId());
			}
		}
		return null;
	}
	
	private void throwError(LdapConnection ldap, Exception e)
			throws BusinessException {
		logger.error(
				"Error while searching for a user with ldap connection {}",
				ldap.getUuid());
		logger.error(e.getMessage());
		logger.debug(e.toString());
		throw new BusinessException(BusinessErrorCode.DIRECTORY_UNAVAILABLE,
				"Couldn't connect to the directory.");
	}

	@Override
	public List<AbstractDomain> findAllDomainsByUserFilter(Account authUser, UserLdapPattern domainUserFilter) {
		preChecks(authUser);
		Validate.notNull(domainUserFilter, "domainUserFilter must be set.");
		List<AbstractDomain> domains = abstractDomainRepository.findAllDomainsByUserFilter(domainUserFilter);
		return domains;
	}

}
