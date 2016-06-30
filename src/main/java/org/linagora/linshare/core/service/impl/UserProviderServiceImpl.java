/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.core.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.UserProviderType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.LdapUserProvider;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserLdapPattern;
import org.linagora.linshare.core.domain.entities.UserProvider;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DomainPatternRepository;
import org.linagora.linshare.core.repository.LdapUserProviderRepository;
import org.linagora.linshare.core.repository.UserProviderRepository;
import org.linagora.linshare.core.service.LDAPQueryService;
import org.linagora.linshare.core.service.UserProviderService;
import org.linagora.linshare.mongo.entities.logs.DomainPatternAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.DomainPatternMto;
import org.linagora.linshare.mongo.repository.AuditAdminMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserProviderServiceImpl extends GenericAdminServiceImpl implements UserProviderService {

	private static final Logger logger = LoggerFactory
			.getLogger(UserProviderServiceImpl.class);

	private final DomainPatternRepository domainPatternRepository;

	private final LDAPQueryService ldapQueryService;

	private final UserProviderRepository userProviderRepository;

	private final LdapUserProviderRepository ldapUserProviderRepository;

	private final AuditAdminMongoRepository mongoRepository;

	public UserProviderServiceImpl(
			DomainPatternRepository domainPatternRepository,
			LDAPQueryService ldapQueryService,
			LdapUserProviderRepository ldapUserProviderRepository,
			UserProviderRepository userProviderRepository,
			AuditAdminMongoRepository mongoRepository) {
		this.domainPatternRepository = domainPatternRepository;
		this.ldapQueryService = ldapQueryService;
		this.userProviderRepository = userProviderRepository;
		this.ldapUserProviderRepository = ldapUserProviderRepository;
		this.mongoRepository = mongoRepository;
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
					"Domain pattern identifier no found.");
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
		Validate.notEmpty(domainPattern.getDescription(), "Pattern's description must be set.");
		Validate.notNull(domainPattern.getCompletionPageSize(), "Pattern's completion page size must be set.");
		Validate.notNull(domainPattern.getCompletionSizeLimit(), "Pattern's completion size limit must be set.");
		Validate.notNull(domainPattern.getSearchPageSize(), "Pattern's search page size must be set.");
		Validate.notNull(domainPattern.getSearchSizeLimit(), "Pattern's search page size must be set.");
		Validate.notEmpty(domainPattern.getAuthCommand(), "Pattern's auth command must be set.");
		Validate.notEmpty(domainPattern.getAutoCompleteCommandOnAllAttributes(), "Patterns's auto complete command on all attributes must be set.");
		Validate.notEmpty(domainPattern.getAutoCompleteCommandOnFirstAndLastName(), "Patterns's auto complete command on first name and last name must be set.");
		Validate.notEmpty(domainPattern.getSearchUserCommand(), "Patterns's search command user must be set.");
		pattern.setDescription(domainPattern.getDescription());
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
	public User findUser(UserProvider up, String mail)
			throws BusinessException {
		if (up != null) {
			if (UserProviderType.LDAP_PROVIDER.equals(up.getType())) {
				LdapUserProvider userProvider = ldapUserProviderRepository.load(up);
				User user = null;
				try {
					user = ldapQueryService.getUser(userProvider.getLdapConnection(),
							userProvider.getBaseDn(), userProvider.getPattern(), mail);
				} catch (NamingException e) {
					throwError(userProvider.getLdapConnection(), e);
				} catch (IOException e) {
					throwError(userProvider.getLdapConnection(), e);
				} catch (org.springframework.ldap.CommunicationException e) {
					throwError(userProvider.getLdapConnection(), e);
				}
				return user;
			} else {
				logger.error("Unsupported UserProviderType : " + up.getType().toString() + ", id : " + up.getId());
			}
		}
		return null;
	}

	@Override
	public List<User> searchUser(UserProvider up, String mail,
			String firstName, String lastName) throws BusinessException {
		if (up != null) {
			if (UserProviderType.LDAP_PROVIDER.equals(up.getType())) {
				LdapUserProvider userProvider = ldapUserProviderRepository.load(up);
				List<User> users = new ArrayList<User>();
				try {
					users = ldapQueryService.searchUser(
							userProvider.getLdapConnection(), userProvider.getBaseDn(),
							userProvider.getPattern(), mail, firstName, lastName);
				} catch (NamingException e) {
					throwError(userProvider.getLdapConnection(), e);
				} catch (IOException e) {
					throwError(userProvider.getLdapConnection(), e);
				} catch (org.springframework.ldap.CommunicationException e) {
					throwError(userProvider.getLdapConnection(), e);
				}
				return users;
			} else {
				logger.error("Unsupported UserProviderType : " + up.getType().toString() + ", id : " + up.getId());
			}
		}
		return null;
	}

	@Override
	public List<User> autoCompleteUser(UserProvider up,
			String pattern) throws BusinessException {
		if (up != null) {
			if (UserProviderType.LDAP_PROVIDER.equals(up.getType())) {
				LdapUserProvider userProvider = ldapUserProviderRepository.load(up);
				List<User> users = new ArrayList<User>();
				try {
					users = ldapQueryService.completeUser(
							userProvider.getLdapConnection(), userProvider.getBaseDn(),
							userProvider.getPattern(), pattern);
				} catch (NamingException e) {
					logger.error(
							"Error while searching for a user with ldap connection {}",
							userProvider.getLdapConnection().getUuid());
					logger.error(e.getMessage());
					logger.debug(e.toString());
				} catch (IOException e) {
					logger.error(
							"Error while searching for a user with ldap connection {}",
							userProvider.getLdapConnection().getUuid());
					logger.error(e.getMessage());
					logger.debug(e.toString());
				} catch (org.springframework.ldap.CommunicationException e) {
					logger.error(
							"Error while searching for a user with ldap connection {}",
							userProvider.getLdapConnection().getUuid());
					logger.error(e.getMessage());
					logger.debug(e.toString());
				}
				return users;
			} else {
				logger.error("Unsupported UserProviderType : " + up.getType().toString() + ", id : " + up.getId());
			}
		}
		return null;
	}

	@Override
	public List<User> autoCompleteUser(UserProvider up,
			String firstName, String lastName) throws BusinessException {
		if (up != null) {
			if (UserProviderType.LDAP_PROVIDER.equals(up.getType())) {
				LdapUserProvider userProvider = ldapUserProviderRepository.load(up);
				List<User> users = new ArrayList<User>();
				try {
					users = ldapQueryService.completeUser(
							userProvider.getLdapConnection(), userProvider.getBaseDn(),
							userProvider.getPattern(), firstName, lastName);
				} catch (NamingException e) {
					logger.error(
							"Error while searching for a user with ldap connection {}",
							userProvider.getLdapConnection().getUuid());
					logger.error(e.getMessage());
					logger.debug(e.toString());
				} catch (IOException e) {
					logger.error(
							"Error while searching for a user with ldap connection {}",
							userProvider.getLdapConnection().getUuid());
					logger.error(e.getMessage());
					logger.debug(e.toString());
				} catch (org.springframework.ldap.CommunicationException e) {
					logger.error(
							"Error while searching for a user with ldap connection {}",
							userProvider.getLdapConnection().getUuid());
					logger.error(e.getMessage());
					logger.debug(e.toString());
				}
				return users;
			} else {
				logger.error("Unsupported UserProviderType : " + up.getType().toString() + ", id : " + up.getId());
			}
		}
		return null;
	}

	@Override
	public Boolean isUserExist(UserProvider up, String mail)
			throws BusinessException {
		if (up != null) {
			if (UserProviderType.LDAP_PROVIDER.equals(up.getType())) {
				LdapUserProvider userProvider = ldapUserProviderRepository.load(up);
				Boolean result = false;
				try {
					result = ldapQueryService.isUserExist(
							userProvider.getLdapConnection(), userProvider.getBaseDn(),
							userProvider.getPattern(), mail);
				} catch (NamingException e) {
					throwError(userProvider.getLdapConnection(), e);
				} catch (IOException e) {
					throwError(userProvider.getLdapConnection(), e);
				} catch (org.springframework.ldap.CommunicationException e) {
					throwError(userProvider.getLdapConnection(), e);
				}
				return result;
			} else {
				logger.error("Unsupported UserProviderType : " + up.getType().toString() + ", id : " + up.getId());
			}
		}
		return null;
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
				} catch (NamingException e) {
					throwError(userProvider.getLdapConnection(), e);
				} catch (IOException e) {
					throwError(userProvider.getLdapConnection(), e);
				} catch (org.springframework.ldap.CommunicationException e) {
					throwError(userProvider.getLdapConnection(), e);
				}
				return user;
			} else {
				logger.error("Unsupported UserProviderType : " + up.getType().toString() + ", id : " + up.getId());
			}
		}
		return null;
	}

	@Override
	public User searchForAuth(UserProvider up, String login)
			throws BusinessException {
		if (up != null) {
			if (UserProviderType.LDAP_PROVIDER.equals(up.getType())) {
				LdapUserProvider userProvider = ldapUserProviderRepository.load(up);
				User user = null;
				try {
					user = ldapQueryService.searchForAuth(userProvider.getLdapConnection(),
							userProvider.getBaseDn(),
							userProvider.getPattern(), login);
				} catch (NamingException e) {
					throwError(userProvider.getLdapConnection(), e);
				} catch (IOException e) {
					throwError(userProvider.getLdapConnection(), e);
				} catch (org.springframework.ldap.CommunicationException e) {
					throwError(userProvider.getLdapConnection(), e);
				}
				return user;
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
}
