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
package org.linagora.linshare.core.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.hibernate.criterion.Order;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.linagora.linshare.auth.oidc.OidcLinShareUserClaims;
import org.linagora.linshare.auth.oidc.OidcTokenWithClaims;
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
import org.linagora.linshare.core.repository.hibernate.LdapConnectionRepositoryImpl;
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

	private final LdapConnectionRepositoryImpl ldapConnectionRepository;

	private final String oidcLdapConnectionUuid;

	private final String oidcLdapPatternUuid;

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
			TwakeGuestUserProviderServiceImpl twakeGuestUserProviderService,
			LdapConnectionRepositoryImpl ldapConnectionRepository,
			String oidcLdapConnectionUuid,
			String oidcLdapPatternUuid) {
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
		this.ldapConnectionRepository = ldapConnectionRepository;
		this.oidcLdapConnectionUuid = oidcLdapConnectionUuid;
		this.oidcLdapPatternUuid = oidcLdapPatternUuid;
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
		if (up == null) {
			return null;
		}
		UserProvider userProvider = userProviderRepository.findByUuid(up.getUuid());

		if (UserProviderType.LDAP_PROVIDER.equals(up.getType())) {
			return getUserFromLdap(domain, mail, (LdapUserProvider) userProvider);

		} else if (UserProviderType.OIDC_PROVIDER.equals(up.getType())) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (!authentication.isAuthenticated()) {
				// it means we are trying to find/create this profile during authentication process.
				if ((OidcTokenWithClaims.class).isAssignableFrom(authentication.getClass())) {
					return buildUserFromClaims(domain, (OIDCUserProvider) userProvider, ((OidcTokenWithClaims) authentication).getClaims());
				}
			} else {
				// probably trying to discover a user.
				logger.debug("UserProviderType.OIDC provider does not supported discovering new user.");
			}
		} else if (UserProviderType.TWAKE_PROVIDER.equals(up.getType())) {
			return twakeUserProviderService.findUser(domain, (TwakeUserProvider) userProvider, mail);
		} else if (UserProviderType.TWAKE_GUEST_PROVIDER.equals(up.getType())) {
			return twakeGuestUserProviderService.findUser(domain, (TwakeGuestUserProvider) userProvider, mail);
		}
		logger.error("Unsupported UserProviderType : " + up.getType().toString() + ", id : " + up.getId());
		return null;
	}

	@Override
	public User findUserByExternalUid(AbstractDomain domain, UserProvider up, @Nonnull String externalUid)
			throws BusinessException {
		if (up == null) {
			return null;
		}
		UserProvider userProvider = userProviderRepository.findByUuid(up.getUuid());

		if (UserProviderType.LDAP_PROVIDER.equals(up.getType())) {
			return getUserFromLdapByExternalUid(domain, externalUid, (LdapUserProvider) userProvider);

		} else if (UserProviderType.OIDC_PROVIDER.equals(up.getType())) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (!authentication.isAuthenticated()) {
				// it means we are trying to find/create this profile during authentication process.
				if ((OidcTokenWithClaims.class).isAssignableFrom(authentication.getClass())) {
					return buildUserFromClaims(domain, (OIDCUserProvider) userProvider, ((OidcTokenWithClaims) authentication).getClaims());
				}
			} else {
				// probably trying to discover a user.
				logger.debug("UserProviderType.OIDC provider does not supported discovering new user.");
			}
		} else if (UserProviderType.TWAKE_PROVIDER.equals(up.getType())) {
			return twakeUserProviderService.findUser(domain, (TwakeUserProvider) userProvider, externalUid);
		} else if (UserProviderType.TWAKE_GUEST_PROVIDER.equals(up.getType())) {
			return twakeGuestUserProviderService.findUser(domain, (TwakeGuestUserProvider) userProvider, externalUid);
		}
		logger.error("Unsupported UserProviderType : " + up.getType().toString() + ", id : " + up.getId());
		return null;
	}


	@Nullable
	private static Internal buildUserFromClaims(AbstractDomain domain, OIDCUserProvider userProvider, OidcLinShareUserClaims claims) {
		// This code is ugly, it is a quick workaround.
		String domainDiscriminator = claims.getDomainDiscriminator();
		if (!userProvider.getDomainDiscriminator().equals(domainDiscriminator)) {
			return null;
		}

		String email = claims.getEmail();
		Validate.notEmpty(email, "Missing required attribute for email.");
		String firstName = claims.getFirstName();
		Validate.notEmpty(firstName, "Missing required attribute for first name.");
		String lastName = claims.getLastName();
		Validate.notEmpty(lastName, "Missing required attribute for last name.");
		Optional<String> externalUuid = Optional.ofNullable(claims.getExternalUid());
		Internal internal = new Internal(firstName, lastName, email, externalUuid.orElse(email));
		internal.setDomain(domain);
		internal.setRole(domain.getDefaultRole());
		internal.setMailLocale(domain.getExternalMailLocale());
		internal.setExternalMailLocale(domain.getExternalMailLocale());
		if (userProvider.getUseRoleClaim()) {
			internal.setRole(Role.toDefaultRole(
					domain.getDefaultRole(),
					claims.getRole()));
		}
		if (userProvider.getUseEmailLocaleClaim()) {
			Language language = Language.toDefaultLanguage(
					domain.getExternalMailLocale(),
					claims.getLocale());
			internal.setMailLocale(language);
			internal.setExternalMailLocale(language);
		}
		return internal;
	}

	@Nullable
	private User getUserFromLdap(AbstractDomain domain, String mail, LdapUserProvider userProvider) {
		LdapUserProvider ldapUserProvider = userProvider;
		User user = null;
		try {
			user = ldapQueryService.getUser(ldapUserProvider.getLdapConnection(),
					ldapUserProvider.getBaseDn(), ldapUserProvider.getPattern(), mail);
			if (user != null) {
				user.setDomain(domain);
				user.setRole(domain.getDefaultRole());
				user.setMailLocale(user.getDomain().getExternalMailLocale());
				user.setExternalMailLocale(user.getDomain().getExternalMailLocale());
			}
		} catch (NamingException | IOException | CommunicationException e) {
			logger.error("Error happen while connecting to ldap " + ldapUserProvider.getLdapConnection(), e);
		}
		return user;
	}

	@Nullable
	private User getUserFromLdapByExternalUid(AbstractDomain domain, String externalUid, LdapUserProvider userProvider) {
		LdapUserProvider ldapUserProvider = userProvider;
		User user = null;
		try {
			user = ldapQueryService.getUserByUid(ldapUserProvider.getLdapConnection(),
					ldapUserProvider.getBaseDn(), ldapUserProvider.getPattern(), externalUid);
			if (user != null) {
				user.setDomain(domain);
				user.setRole(domain.getDefaultRole());
				user.setMailLocale(user.getDomain().getExternalMailLocale());
				user.setExternalMailLocale(user.getDomain().getExternalMailLocale());
			}
		} catch (NamingException | IOException | CommunicationException e) {
			logger.error("Error happen while connecting to ldap " + ldapUserProvider.getLdapConnection(), e);
		}
		return user;
	}

	@Override
	public List<User> searchUser(AbstractDomain domain, UserProvider up,
			String mail, String firstName, String lastName) throws BusinessException {
		if (up != null) {
			UserProvider userProvider = userProviderRepository.findByUuid(up.getUuid());
			if (UserProviderType.LDAP_PROVIDER.equals(up.getType())) {
				LdapUserProvider ldapUserProvider = (LdapUserProvider) userProvider;
				List<User> users = new ArrayList<User>();
				try {
					users = ldapQueryService.searchUser(
							ldapUserProvider.getLdapConnection(), ldapUserProvider.getBaseDn(),
							ldapUserProvider.getPattern(), mail, firstName, lastName);
				} catch (NamingException  | IOException | CommunicationException e) {
					logger.error("Error happen while connecting to ldap " + ldapUserProvider.getLdapConnection(), e);
				}
				return users;
			} else if (UserProviderType.OIDC_PROVIDER.equals(up.getType())) {
				PageContainer<User> container = new PageContainer<>(0,50);
				container = userRepository.findAll(Lists.newArrayList(domain), Order.asc("modificationDate"), mail, firstName,
						lastName, null, null, null, null, null, Set.of(), container);
				List <User> users = container.getPageResponse().getContent();

				return addSearchedLdapUsersFromConfig(domain, users, mail, firstName, lastName);
			} else if (UserProviderType.TWAKE_PROVIDER.equals(up.getType())) {
				return twakeUserProviderService.searchUser(domain, (TwakeUserProvider) userProvider, mail, firstName, lastName);
			} else if (UserProviderType.TWAKE_GUEST_PROVIDER.equals(up.getType())) {
				return twakeGuestUserProviderService.searchUser(domain, (TwakeGuestUserProvider) userProvider, mail, firstName, lastName);
			} else {
				logger.error("Unsupported UserProviderType : " + up.getType().toString() + ", id : " + up.getId());
			}
		}
		return Lists.newArrayList();
	}

	private List<User> addSearchedLdapUsersFromConfig(AbstractDomain domain, List<User> users,
			 String mail, String firstName, String lastName) {
		LdapConnection oidcLdapConnection = !StringUtils.isBlank(oidcLdapConnectionUuid)
				? ldapConnectionRepository.findByUuid(oidcLdapConnectionUuid)
				: null;
		UserLdapPattern oidcLdapPattern = !StringUtils.isBlank(oidcLdapPatternUuid)
				? domainPatternRepository.findByUuid(oidcLdapPatternUuid)
				: null;

		if (oidcLdapConnection != null && oidcLdapPattern != null) {
			try {
				HashSet<User> allUsers = new HashSet<>(users);

				allUsers.addAll(ldapQueryService.searchUser(
						oidcLdapConnection, getBaseDn(domain),
						oidcLdapPattern, mail, firstName, lastName));

				return List.copyOf(allUsers);
			} catch (NamingException | IOException | CommunicationException e) {
				logger.error(
						"Error while searching for a user with ldap connection {}",
						oidcLdapConnection.getUuid());
				logger.error(e.getMessage());
				logger.debug(e.toString());
			}
		}
		return users;
	}

	@Override
	public List<User> autoCompleteUser(AbstractDomain domain,
			UserProvider up, String pattern) throws BusinessException {
		if (up != null) {
			UserProvider userProvider = userProviderRepository.findByUuid(up.getUuid());
			if (UserProviderType.LDAP_PROVIDER.equals(up.getType())) {
				LdapUserProvider ldapUserProvider = (LdapUserProvider) userProvider;
				List<User> users = new ArrayList<User>();
				try {
					users = ldapQueryService.completeUser(
							ldapUserProvider.getLdapConnection(), ldapUserProvider.getBaseDn(),
							ldapUserProvider.getPattern(), pattern);
				} catch (NamingException  | IOException | CommunicationException e) {
					logger.error(
							"Error while searching for a user with ldap connection {}",
							ldapUserProvider.getLdapConnection().getUuid());
					logger.error(e.getMessage());
					logger.debug(e.toString());
				}
				users.stream().map(user -> {user.setDomain(domain); return user;});
				return users;
			} else if (UserProviderType.OIDC_PROVIDER.equals(up.getType())) {
				PageContainer<User> container = new PageContainer<>(0,50);
				container = userRepository.findAll(Lists.newArrayList(domain), Order.asc("modificationDate"), pattern, null,
						null, null, null, null, null, null, Set.of(), container);
				List<User> users = container.getPageResponse().getContent();
				return addCompletedLdapUsersFromConfig(domain, users, pattern, null, null);
			} else if (UserProviderType.TWAKE_PROVIDER.equals(up.getType())) {
				return twakeUserProviderService.autoCompleteUser(domain, (TwakeUserProvider) userProvider, pattern);
			} else if (UserProviderType.TWAKE_GUEST_PROVIDER.equals(up.getType())) {
				return twakeGuestUserProviderService.autoCompleteUser(domain, (TwakeGuestUserProvider) userProvider, pattern);
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
			UserProvider userProvider = userProviderRepository.findByUuid(up.getUuid());
			if (UserProviderType.LDAP_PROVIDER.equals(up.getType())) {
				LdapUserProvider ldapUserProvider = (LdapUserProvider) userProvider;
				List<User> users = new ArrayList<User>();
				try {
					users = ldapQueryService.completeUser(
							ldapUserProvider.getLdapConnection(), ldapUserProvider.getBaseDn(),
							ldapUserProvider.getPattern(), firstName, lastName);
				} catch (NamingException | IOException | CommunicationException e) {
					logger.error(
							"Error while searching for a user with ldap connection {}",
							ldapUserProvider.getLdapConnection().getUuid());
					logger.error(e.getMessage());
					logger.debug(e.toString());
				}
				users.stream().map(user -> {user.setDomain(domain); return user;});
				return users;
			} else if (UserProviderType.OIDC_PROVIDER.equals(up.getType())) {
				PageContainer<User> container = new PageContainer<>(0,50);
				container = userRepository.findAll(Lists.newArrayList(domain), Order.asc("modificationDate"), null, firstName,
						lastName, null, null, null, null, null, Set.of(),
						container);
				List<User> users = container.getPageResponse().getContent();
				return addCompletedLdapUsersFromConfig(domain, users, null, firstName, lastName);
			} else if (UserProviderType.TWAKE_PROVIDER.equals(up.getType())) {
				return twakeUserProviderService.autoCompleteUser(domain, (TwakeUserProvider) userProvider, firstName, lastName);
			} else if (UserProviderType.TWAKE_GUEST_PROVIDER.equals(up.getType())) {
				return twakeGuestUserProviderService.autoCompleteUser(domain, (TwakeGuestUserProvider) userProvider, firstName, lastName);
			} else {
				logger.error("Unsupported UserProviderType : " + up.getType().toString() + ", id : " + up.getId());
			}
		}
		return Lists.newArrayList();
	}

	private List<User> addCompletedLdapUsersFromConfig(AbstractDomain domain, List<User> users,
			 @Nullable String pattern, @Nullable String firstName, @Nullable String lastName) {
		LdapConnection oidcLdapConnection = !StringUtils.isBlank(oidcLdapConnectionUuid)
				? ldapConnectionRepository.findByUuid(oidcLdapConnectionUuid)
				: null;
		UserLdapPattern oidcLdapPattern = !StringUtils.isBlank(oidcLdapPatternUuid)
				? domainPatternRepository.findByUuid(oidcLdapPatternUuid)
				: null;

		if (oidcLdapConnection != null && oidcLdapPattern != null) {
			try {
				HashSet<User> allUsers = new HashSet<>(users);

				if (!StringUtils.isBlank(pattern)){
					allUsers.addAll(ldapQueryService.completeUser(
							oidcLdapConnection, getBaseDn(domain),
							oidcLdapPattern, pattern));

				} else if (!StringUtils.isBlank(firstName) && !StringUtils.isBlank(lastName)){
					allUsers.addAll(ldapQueryService.completeUser(
							oidcLdapConnection, getBaseDn(domain),
							oidcLdapPattern, firstName, lastName));
				}
				return List.copyOf(allUsers);
			} catch (NamingException | IOException | CommunicationException e) {
				logger.error(
						"Error while searching for a user with ldap connection {}",
						oidcLdapConnection.getUuid());
				logger.error(e.getMessage());
				logger.debug(e.toString());
			}
		}
		return users;
	}

	@Nonnull
	private static String getBaseDn(AbstractDomain domain) {
		return "ou=" + domain.getLabel().toLowerCase() + ",dc=linshare,dc=org";
	}

	@Override
	public Boolean isUserExist(AbstractDomain domain, UserProvider up, String mail)
			throws BusinessException {
		if (up == null) {
			return false;
		}

		UserProvider userProvider = userProviderRepository.findByUuid(up.getUuid());
		if (UserProviderType.LDAP_PROVIDER.equals(up.getType())) {
			LdapUserProvider ldapUserProvider = (LdapUserProvider) userProvider;
			try {
				return ldapQueryService.isUserExist(ldapUserProvider.getLdapConnection(), ldapUserProvider.getBaseDn(),
						ldapUserProvider.getPattern(), mail);
			} catch (NamingException | IOException | CommunicationException e) {
				logger.error("Error happen while connecting to ldap " + ldapUserProvider.getLdapConnection(), e);
				return false;
			}
		} else if (UserProviderType.OIDC_PROVIDER.equals(up.getType())) {
			final User user = userRepository.findByDomainAndMail(domain.getUuid(), mail);
			return user != null;
		} else if (UserProviderType.TWAKE_PROVIDER.equals(up.getType())) {
			return twakeUserProviderService.isUserExist(domain, (TwakeUserProvider) userProvider, mail);
		} else if (UserProviderType.TWAKE_GUEST_PROVIDER.equals(up.getType())) {
			return twakeGuestUserProviderService.isUserExist(domain, (TwakeGuestUserProvider) userProvider, mail);
		} else {
			logger.error("Unsupported UserProviderType : " + up.getType().toString() + ", id : " + up.getId());
			return false;
		}
	}

	@Override
	public User auth(UserProvider up, String login,
			String userPasswd) throws BusinessException {
		if (up != null) {
			UserProvider userProvider = userProviderRepository.findByUuid(up.getUuid());
			if (UserProviderType.LDAP_PROVIDER.equals(up.getType())) {
				LdapUserProvider ldapUserProvider = (LdapUserProvider) userProvider;
				User user = null;
				try {
					user = ldapQueryService.auth(ldapUserProvider.getLdapConnection(),
							ldapUserProvider.getBaseDn(),
							ldapUserProvider.getPattern(),
							login, userPasswd);
				} catch (NamingException | IOException | CommunicationException e) {
					logger.error("Error happen while connecting to ldap " + ldapUserProvider.getLdapConnection(), e);
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
			UserProvider userProvider = userProviderRepository.findByUuid(up.getUuid());

			if (UserProviderType.LDAP_PROVIDER.equals(up.getType())) {
				return ldapSearchForAuth(domain, login, (LdapUserProvider) userProvider);

			} else if (UserProviderType.OIDC_PROVIDER.equals(up.getType())) {
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				if(authentication != null && !authentication.isAuthenticated()) {
					// It means we are trying to find this profile during authentication process.
					return oidcSearchForAuth(domain, (OIDCUserProvider) userProvider, authentication);
				} else {
					logger.debug("Using UserProviderType.OIDC provider outside authentication is not supported.");
					logger.debug("authentication context is null or account is not authenticated.");
				}

			} else if (UserProviderType.TWAKE_PROVIDER.equals(up.getType())) {
				return twakeUserProviderService.searchForAuth(domain, (TwakeUserProvider) userProvider, login);
			} else if (UserProviderType.TWAKE_GUEST_PROVIDER.equals(up.getType())) {
				return twakeGuestUserProviderService.searchForAuth(domain, (TwakeGuestUserProvider) userProvider, login);
			} else {
				logger.error("Unsupported UserProviderType : " + up.getType().toString() + ", id : " + up.getId());
			}
		}
		return null;
	}

	@Nullable
	private User ldapSearchForAuth(AbstractDomain domain, String login, LdapUserProvider ldapUserProvider) {
		try {
			User user = ldapQueryService.searchForAuth(ldapUserProvider.getLdapConnection(),
					ldapUserProvider.getBaseDn(),
					ldapUserProvider.getPattern(), login);
			if (user != null) {
				user.setDomain(domain);
			}
			return user;
		} catch (NamingException | IOException | CommunicationException e) {
			logger.error("Error happen while connecting to ldap " + ldapUserProvider.getLdapConnection(), e);
			return null;
		}
	}

	@Nullable
	private static Internal oidcSearchForAuth(AbstractDomain domain, OIDCUserProvider userProvider, Authentication authentication) {
		// It was the auto discover feature of LDAP, but it does not exist for OIDC.
		// This code is ugly workaround
		if (!(OidcTokenWithClaims.class).isAssignableFrom(authentication.getClass())) {
			logger.trace("It is not a OidcOpaqueAuthenticationToken or OidcTokenWithClaims class: {}", authentication.getClass());
			return null;
		}

		OidcTokenWithClaims jwtAuthentication = (OidcTokenWithClaims) authentication;
		String domainDiscriminator = jwtAuthentication.getClaims().getDomainDiscriminator();
		logger.trace("domainDiscriminator : {}", domainDiscriminator);
		if (!userProvider.getDomainDiscriminator().equals(domainDiscriminator)) {
			// if this user does not belong to this domain, we ignore him.
			logger.trace("Skipped. Provided oidcDomainIdentifier does not match current domain: domainDiscriminator={}, domain={}", domainDiscriminator, domain.getUuid());
			return null;
		}

		String email = jwtAuthentication.getClaims().getEmail();
		Validate.notEmpty(email, "Missing required attribute for email.");
		Internal internal = new Internal(null, null, email, null);
		internal.setDomain(domain);
		return internal;
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
