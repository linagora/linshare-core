/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.DomainPattern;
import org.linagora.linshare.core.domain.entities.LDAPConnection;
import org.linagora.linshare.core.domain.entities.LdapUserProvider;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DomainPatternRepository;
import org.linagora.linshare.core.repository.LDAPConnectionRepository;
import org.linagora.linshare.core.repository.UserProviderRepository;
import org.linagora.linshare.core.service.LDAPQueryService;
import org.linagora.linshare.core.service.UserProviderService;
import org.linagora.linshare.core.utils.LsIdValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserProviderServiceImpl implements UserProviderService {

	private static final Logger logger = LoggerFactory
			.getLogger(UserProviderServiceImpl.class);

	private final LDAPConnectionRepository ldapConnectionRepository;
	private final DomainPatternRepository domainPatternRepository;
	private final LDAPQueryService ldapQueryService;
	private final UserProviderRepository userProviderRepository;

	public UserProviderServiceImpl(
			LDAPConnectionRepository ldapConnectionRepository,
			DomainPatternRepository domainPatternRepository,
			LDAPQueryService ldapQueryService,
			UserProviderRepository userProviderRepository) {
		this.ldapConnectionRepository = ldapConnectionRepository;
		this.domainPatternRepository = domainPatternRepository;
		this.ldapQueryService = ldapQueryService;
		this.userProviderRepository = userProviderRepository;
	}

	@Override
	public DomainPattern createDomainPattern(DomainPattern domainPattern)
			throws BusinessException {
		Validate.notEmpty(domainPattern.getIdentifier(),
				"domain pattern identifier must be set.");
		if (!LsIdValidator.isValid(domainPattern.getIdentifier())) {
			throw new BusinessException(BusinessErrorCode.LDAP_CONNECTION_ID_BAD_FORMAT,
					"This new domain pattern identifier should only contains the following characters : "
							+ LsIdValidator.getAllowedCharacters() + ".");
		}
		if (domainPatternRepository.findById(domainPattern.getIdentifier()) != null) {
			throw new BusinessException(
					BusinessErrorCode.DOMAIN_PATTERN_ID_ALREADY_EXISTS,
					"This new domain pattern identifier already exists.");
		}
		DomainPattern createdDomain = domainPatternRepository
				.create(domainPattern);
		return createdDomain;
	}

	@Override
	public LDAPConnection createLDAPConnection(LDAPConnection ldapConnection)
			throws BusinessException {
		Validate.notEmpty(ldapConnection.getIdentifier(),
				"ldap connection identifier must be set.");
		if (!LsIdValidator.isValid(ldapConnection.getIdentifier())) {
			throw new BusinessException(
					BusinessErrorCode.LDAP_CONNECTION_ID_BAD_FORMAT,
					"This new ldap connection identifier should only contains the following characters : "
							+ LsIdValidator.getAllowedCharacters() + ".");
		}
		if (ldapConnectionRepository.findById(ldapConnection.getIdentifier()) != null) {
			throw new BusinessException(
					BusinessErrorCode.LDAP_CONNECTION_ID_ALREADY_EXISTS,
					"This new ldap connection identifier already exists.");
		}
		LDAPConnection createdLDAPConnection = ldapConnectionRepository
				.create(ldapConnection);
		return createdLDAPConnection;
	}

	@Override
	public LDAPConnection retrieveLDAPConnection(String identifier)
			throws BusinessException {
		return ldapConnectionRepository.findById(identifier);
	}

	@Override
	public DomainPattern retrieveDomainPattern(String identifier)
			throws BusinessException {
		DomainPattern pattern = domainPatternRepository.findById(identifier);
		if (pattern == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_PATTERN_NOT_FOUND,
					"Domain pattern identifier no found.");
		}
		return pattern;
	}

	@Override
	public void deleteConnection(String connectionToDelete)
			throws BusinessException {
		if (!connectionIsDeletable(connectionToDelete)) {
			throw new BusinessException(
					"Cannot delete connection because still used by domains");
		}

		LDAPConnection conn = retrieveLDAPConnection(connectionToDelete);
		if (conn == null) {
			logger.error("Ldap connexion not found: " + connectionToDelete);
		} else {
			logger.debug("delete ldap connexion : " + connectionToDelete);
			ldapConnectionRepository.delete(conn);
		}
	}

	@Override
	public boolean connectionIsDeletable(String connectionToDelete) {
		List<LdapUserProvider> list = userProviderRepository.findAll();
		boolean used = false;
		for (LdapUserProvider ldapUserProvider : list) {
			if (ldapUserProvider.getLdapconnexion().getIdentifier()
					.equals(connectionToDelete)) {
				used = true;
				break;
			}
		}
		return (!used);
	}

	@Override
	public void deletePattern(String patternToDelete) throws BusinessException {

		if (!patternIsDeletable(patternToDelete)) {
			throw new BusinessException(
					"Cannot delete pattern because still used by domains");
		}

		DomainPattern pattern = retrieveDomainPattern(patternToDelete);
		domainPatternRepository.delete(pattern);
	}

	@Override
	public boolean patternIsDeletable(String patternToDelete) {
		List<LdapUserProvider> list = userProviderRepository.findAll();
		boolean used = false;
		for (LdapUserProvider ldapUserProvider : list) {
			if (ldapUserProvider.getPattern().getIdentifier()
					.equals(patternToDelete)) {
				used = true;
				break;
			}
		}
		return (!used);
	}

	@Override
	public List<DomainPattern> findAllDomainPattern() {
		return domainPatternRepository.findAll();
	}

	@Override
	public List<DomainPattern> findAllUserDomainPattern()
			throws BusinessException {
		return domainPatternRepository.findAllUserDomainPattern();
	}

	@Override
	public List<DomainPattern> findAllSystemDomainPattern()
			throws BusinessException {
		return domainPatternRepository.findAllSystemDomainPattern();
	}

	@Override
	public List<LDAPConnection> findAllLDAPConnections()
			throws BusinessException {
		return ldapConnectionRepository.findAll();
	}

	@Override
	public LDAPConnection updateLDAPConnection(LDAPConnection ldapConnection)
			throws BusinessException {
		LDAPConnection ldapConn = ldapConnectionRepository
				.findById(ldapConnection.getIdentifier());
		if (ldapConn == null) {
			throw new BusinessException(BusinessErrorCode.LDAP_CONNECTION_NOT_FOUND, "no such ldap connection");
		}
		ldapConn.setProviderUrl(ldapConnection.getProviderUrl());
		ldapConn.setSecurityAuth(ldapConnection.getSecurityAuth());
		ldapConn.setSecurityCredentials(ldapConnection.getSecurityCredentials());
		ldapConn.setSecurityPrincipal(ldapConnection.getSecurityPrincipal());
		return ldapConnectionRepository.update(ldapConn);
	}

	@Override
	public DomainPattern updateDomainPattern(DomainPattern domainPattern)
			throws BusinessException {
		DomainPattern pattern = domainPatternRepository.findById(domainPattern
				.getIdentifier());
		if (pattern == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_PATTERN_NOT_FOUND, "no such domain pattern");
		}
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
				.get(DomainPattern.USER_FIRST_NAME)
				.setAttribute(
						domainPattern.getAttributes()
								.get(DomainPattern.USER_FIRST_NAME)
								.getAttribute());
		pattern.getAttributes()
				.get(DomainPattern.USER_LAST_NAME)
				.setAttribute(
						domainPattern.getAttributes()
								.get(DomainPattern.USER_LAST_NAME)
								.getAttribute());
		pattern.getAttributes()
				.get(DomainPattern.USER_MAIL)
				.setAttribute(
						domainPattern.getAttributes()
								.get(DomainPattern.USER_MAIL).getAttribute());
		pattern.getAttributes()
				.get(DomainPattern.USER_UID)
				.setAttribute(
						domainPattern.getAttributes()
								.get(DomainPattern.USER_UID).getAttribute());
		return domainPatternRepository.update(pattern);
	}

	@Override
	public void create(LdapUserProvider userProvider) throws BusinessException {
		userProviderRepository.create(userProvider);
	}

	@Override
	public void delete(LdapUserProvider userProvider) throws BusinessException {
		userProviderRepository.delete(userProvider);
	}

	@Override
	public void update(LdapUserProvider userProvider) throws BusinessException {
		userProviderRepository.update(userProvider);
	}

	@Override
	public User findUser(LdapUserProvider userProvider, String mail)
			throws BusinessException {
		LdapUserProvider p = userProvider;
		if (p == null) {
			return null;
		}
		User user = null;
		try {
			user = ldapQueryService.getUser(userProvider.getLdapconnexion(),
					userProvider.getBaseDn(), userProvider.getPattern(), mail);
		} catch (NamingException e) {
			throwError(userProvider.getLdapconnexion(), e);
		} catch (IOException e) {
			throwError(userProvider.getLdapconnexion(), e);
		} catch (org.springframework.ldap.CommunicationException e) {
			throwError(userProvider.getLdapconnexion(), e);
		}
		return user;
	}

	@Override
	public List<User> searchUser(LdapUserProvider userProvider, String mail,
			String firstName, String lastName) throws BusinessException {
		List<User> users = new ArrayList<User>();
		try {
			users = ldapQueryService.searchUser(userProvider.getLdapconnexion(),
					userProvider.getBaseDn(), userProvider.getPattern(), mail,
					firstName, lastName);
		} catch (NamingException e) {
			throwError(userProvider.getLdapconnexion(), e);
		} catch (IOException e) {
			throwError(userProvider.getLdapconnexion(), e);
		} catch (org.springframework.ldap.CommunicationException e) {
			throwError(userProvider.getLdapconnexion(), e);
		}
		return users;
	}

	@Override
	public List<User> autoCompleteUser(LdapUserProvider userProvider,
			String pattern) throws BusinessException {
		List<User> users = new ArrayList<User>();
		try {
			users = ldapQueryService.completeUser(userProvider.getLdapconnexion(),
					userProvider.getBaseDn(), userProvider.getPattern(), pattern);
		} catch (NamingException e) {
			throwError(userProvider.getLdapconnexion(), e);
		} catch (IOException e) {
			throwError(userProvider.getLdapconnexion(), e);
		} catch (org.springframework.ldap.CommunicationException e) {
			throwError(userProvider.getLdapconnexion(), e);
		}
		return users;
	}

	@Override
	public List<User> autoCompleteUser(LdapUserProvider userProvider,
			String firstName, String lastName) throws BusinessException {
		List<User> users = new ArrayList<User>();
		try {
			users = ldapQueryService.completeUser(userProvider.getLdapconnexion(),
				userProvider.getBaseDn(), userProvider.getPattern(), firstName,
				lastName);
		} catch (NamingException e) {
			throwError(userProvider.getLdapconnexion(), e);
		} catch (IOException e) {
			throwError(userProvider.getLdapconnexion(), e);
		} catch (org.springframework.ldap.CommunicationException e) {
			throwError(userProvider.getLdapconnexion(), e);
		}
		return users;
	}

	@Override
	public Boolean isUserExist(LdapUserProvider userProvider, String mail)
			throws BusinessException {
		Boolean result = false;
		try {
			result = ldapQueryService.isUserExist(userProvider.getLdapconnexion(),
					userProvider.getBaseDn(), userProvider.getPattern(), mail);
		} catch (NamingException e) {
			throwError(userProvider.getLdapconnexion(), e);
		} catch (IOException e) {
			throwError(userProvider.getLdapconnexion(), e);
		} catch (org.springframework.ldap.CommunicationException e) {
			throwError(userProvider.getLdapconnexion(), e);
		}
		return result;
	}

	@Override
	public User auth(LdapUserProvider userProvider, String login,
			String userPasswd) throws BusinessException {
		LdapUserProvider p = userProvider;
		if (p == null) {
			return null;
		}
		User user = null;
		try {
			user = ldapQueryService.auth(p.getLdapconnexion(), p.getBaseDn(),
					p.getPattern(), login, userPasswd);
		} catch (NamingException e) {
			throwError(userProvider.getLdapconnexion(), e);
		} catch (IOException e) {
			throwError(userProvider.getLdapconnexion(), e);
		} catch (org.springframework.ldap.CommunicationException e) {
			throwError(userProvider.getLdapconnexion(), e);
		}
		return user;
	}

	@Override
	public User searchForAuth(LdapUserProvider userProvider, String login)
			throws BusinessException {
		LdapUserProvider p = userProvider;
		if (p == null) {
			return null;
		}
		User user = null;
		try {
			user = ldapQueryService.searchForAuth(p.getLdapconnexion(),
					p.getBaseDn(), p.getPattern(), login);
		} catch (NamingException e) {
			throwError(userProvider.getLdapconnexion(), e);
		} catch (IOException e) {
			throwError(userProvider.getLdapconnexion(), e);
		} catch (org.springframework.ldap.CommunicationException e) {
			throwError(userProvider.getLdapconnexion(), e);
		}
		return user;
	}

	private void throwError(LDAPConnection ldap, Exception e) throws BusinessException {
		logger.error(
				"Error while searching for a user with ldap connection {}",
				ldap.getIdentifier());
		logger.error(e.getMessage());
		logger.debug(e.toString());
		throw new BusinessException(
				BusinessErrorCode.DIRECTORY_UNAVAILABLE,
				"Couldn't connect to the directory.");
	}

	@Override
	public List<String> findAllDomainPatternIdentifiers() {
		List<String> list = new ArrayList<String>();
		for (DomainPattern d : domainPatternRepository.findAll()) {
			list.add(d.getIdentifier());
		}
		return list;
	}

	@Override
	public List<String> findAllUserDomainPatternIdentifiers() {
		List<String> list = new ArrayList<String>();
		for (DomainPattern d : domainPatternRepository
				.findAllUserDomainPattern()) {
			list.add(d.getIdentifier());
		}
		return list;
	}

	@Override
	public List<String> findAllSystemDomainPatternIdentifiers() {
		List<String> list = new ArrayList<String>();
		for (DomainPattern d : domainPatternRepository
				.findAllSystemDomainPattern()) {
			list.add(d.getIdentifier());
		}
		return list;
	}

	@Override
	public List<String> findAllLDAPConnectionIdentifiers() {
		List<String> list = new ArrayList<String>();
		for (LDAPConnection c : ldapConnectionRepository.findAll()) {
			list.add(c.getIdentifier());
		}
		return list;
	}
}
