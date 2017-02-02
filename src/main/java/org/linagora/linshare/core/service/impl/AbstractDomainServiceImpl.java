/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2016. Contribute to
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DomainAccessPolicyBusinessService;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.DomainQuotaBusinessService;
import org.linagora.linshare.core.business.service.ContainerQuotaBusinessService;
import org.linagora.linshare.core.business.service.MailConfigBusinessService;
import org.linagora.linshare.core.business.service.MimePolicyBusinessService;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MimePolicy;
import org.linagora.linshare.core.domain.entities.SubDomain;
import org.linagora.linshare.core.domain.entities.TopDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserProvider;
import org.linagora.linshare.core.domain.entities.WelcomeMessages;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.DomainPolicyService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.UserProviderService;
import org.linagora.linshare.core.service.WelcomeMessagesService;
import org.linagora.linshare.mongo.entities.logs.DomainAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.DomainMto;
import org.linagora.linshare.mongo.repository.AuditAdminMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class AbstractDomainServiceImpl implements AbstractDomainService {

	private static final Logger logger = LoggerFactory
			.getLogger(AbstractDomainServiceImpl.class);

	private final AbstractDomainRepository abstractDomainRepository;
	private final DomainPolicyService domainPolicyService;
	private final FunctionalityReadOnlyService functionalityReadOnlyService;
	private final UserProviderService userProviderService;
	private final UserRepository<User> userRepository;
	private final DomainBusinessService domainBusinessService;
	private final MimePolicyBusinessService mimePolicyBusinessService;
	private final MailConfigBusinessService mailConfigBusinessService;
	private final WelcomeMessagesService welcomeMessagesService;
	private final AuditAdminMongoRepository auditMongoRepository;
	final private DomainAccessPolicyBusinessService domainAccessPolicyBusinessService;
	private final DomainQuotaBusinessService domainQuotaBusinessService;
	private final ContainerQuotaBusinessService containerQuotaBusinessService;

	public AbstractDomainServiceImpl(
			final AbstractDomainRepository abstractDomainRepository,
			final DomainPolicyService domainPolicyService,
			final FunctionalityReadOnlyService functionalityReadOnlyService,
			final UserProviderService userProviderService,
			final UserRepository<User> userRepository,
			final DomainBusinessService domainBusinessService,
			final MimePolicyBusinessService mimePolicyBusinessService,
			final MailConfigBusinessService mailConfigBusinessService,
			final WelcomeMessagesService welcomeMessagesService,
			final AuditAdminMongoRepository auditMongoRepository,
			final DomainAccessPolicyBusinessService domainAccessPolicyBusinessService,
			final DomainQuotaBusinessService domainQuotaBusinessService,
			final ContainerQuotaBusinessService containerQuotaBusinessService) {
		super();
		this.abstractDomainRepository = abstractDomainRepository;
		this.domainPolicyService = domainPolicyService;
		this.userProviderService = userProviderService;
		this.userRepository = userRepository;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.domainBusinessService = domainBusinessService;
		this.mimePolicyBusinessService = mimePolicyBusinessService;
		this.mailConfigBusinessService = mailConfigBusinessService;
		this.welcomeMessagesService = welcomeMessagesService;
		this.auditMongoRepository = auditMongoRepository;
		this.domainAccessPolicyBusinessService = domainAccessPolicyBusinessService;
		this.domainQuotaBusinessService = domainQuotaBusinessService;
		this.containerQuotaBusinessService = containerQuotaBusinessService;
	}

	@Override
	public AbstractDomain getUniqueRootDomain() throws BusinessException {
		return abstractDomainRepository.getUniqueRootDomain();
	}

	private AbstractDomain createDomain(Account actor,
			AbstractDomain domain, AbstractDomain parentDomain) throws BusinessException {
		if (!actor.hasSuperAdminRole()) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Only root is authorized to create domains.");
		}
		if (domain.getPolicy() == null) {
			throw new BusinessException(
					BusinessErrorCode.DOMAIN_POLICY_NOT_FOUND,
					"This new domain has no domain policy.");
		}
		if (domain.getCurrentMailConfiguration() == null) {
			throw new BusinessException(BusinessErrorCode.MAILCONFIG_NOT_FOUND,
					"This domain has no mail config.");
		} else {
			MailConfig mailConfig = mailConfigBusinessService.findByUuid(domain
					.getCurrentMailConfiguration().getUuid());
			domain.setCurrentMailConfiguration(mailConfig);
		}
		if (domain.getMimePolicy() == null) {
			throw new BusinessException(BusinessErrorCode.MIME_NOT_FOUND,
					"This domain has no mime policy.");
		} else {
			MimePolicy mimePolicy = mimePolicyBusinessService.find(domain
					.getMimePolicy().getUuid());
			domain.setMimePolicy(mimePolicy);
		}
		if (domain.getCurrentWelcomeMessage() == null) {
			throw new BusinessException(
					BusinessErrorCode.WELCOME_MESSAGES_NOT_FOUND,
					"This domain has no current welcome message");
		}
		if (domain.getDescription() == null) {
			domain.setDescription("");
		}
		DomainPolicy policy = domainPolicyService.find(domain.getPolicy()
				.getUuid());

		if (policy == null) {
			throw new BusinessException(
					BusinessErrorCode.DOMAIN_POLICY_NOT_FOUND,
					"This new domain has a wrong domain policy identifier.");
		}
		domain.setPolicy(policy);
		domain.setParentDomain(parentDomain);
		domain.setAuthShowOrder(new Long(1));
		// Object creation
		domain = abstractDomainRepository.create(domain);
		createDomainQuotaAndContainerQuota(domain);
		// Update ancestor relation
		parentDomain.addSubdomain(domain);
		abstractDomainRepository.update(parentDomain);
		DomainAuditLogEntry log = new DomainAuditLogEntry(actor, LogAction.CREATE, AuditLogEntryType.DOMAIN, domain);
		auditMongoRepository.insert(log);
		return domain;
	}

	@Override
	public TopDomain createTopDomain(Account actor, TopDomain topDomain)
			throws BusinessException {
		if (!(topDomain.getDefaultRole().equals(Role.SIMPLE) || topDomain.getDefaultRole().equals(Role.ADMIN))) {
			topDomain.setDefaultRole(Role.SIMPLE);
		}
		logger.debug("TopDomain creation attempt : " + topDomain.toString());
		return (TopDomain) createDomain(actor, topDomain, getUniqueRootDomain());
	}

	@Override
	public SubDomain createSubDomain(Account actor, SubDomain subDomain)
			throws BusinessException {
		logger.debug("SubDomain creation attempt : " + subDomain.toString());
		if (!(subDomain.getDefaultRole().equals(Role.SIMPLE) || subDomain.getDefaultRole().equals(Role.ADMIN))) {
			subDomain.setDefaultRole(Role.SIMPLE);
		}
		if (subDomain.getParentDomain() == null
				|| subDomain.getParentDomain().getUuid() == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_ID_NOT_FOUND,
					"This new domain has no parent domain defined.");
		}
		AbstractDomain parentDomain = retrieveDomain(subDomain
				.getParentDomain().getUuid());
		if (parentDomain == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_ID_NOT_FOUND,
					"Parent domain not found.");
		}
		if (!parentDomain.getDomainType().equals(DomainType.TOPDOMAIN)) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_INVALID_TYPE,
					"You must create a subdomain inside a TopDomain.");
		}
		return (SubDomain) createDomain(actor, subDomain, parentDomain);
	}

	@Override
	public GuestDomain createGuestDomain(Account actor, GuestDomain guestDomain)
			throws BusinessException {
		logger.debug("SubDomain creation attempt : " + guestDomain.toString());
		guestDomain.setDefaultRole(Role.SIMPLE);
		if (guestDomain.getParentDomain() == null
				|| guestDomain.getParentDomain().getUuid() == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_ID_NOT_FOUND,
					"This new domain has no parent domain defined.");
		}
		AbstractDomain parentDomain = retrieveDomain(guestDomain
				.getParentDomain().getUuid());
		if (parentDomain == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_ID_NOT_FOUND,
					"Parent domain not found.");
		}
		if (!parentDomain.getDomainType().equals(DomainType.TOPDOMAIN)) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_INVALID_TYPE,
					"You must create a guest domain inside a TopDomain.");
		}
		return (GuestDomain) createDomain(actor, guestDomain, parentDomain);
	}

	@Override
	public AbstractDomain retrieveDomain(String identifier) {
		// HACK
		try {
			return domainBusinessService.findById(identifier);
		} catch (BusinessException e) {
			return null;
		}
	}

	@Override
	public AbstractDomain findById(String identifier) throws BusinessException {
		Validate.notEmpty(identifier, "Domain identifier must be set.");
		AbstractDomain domain = domainBusinessService.findById(identifier);
//		AKO : Find a hack to find the actor
//		DomainAuditLogEntry log = new DomainAuditLogEntry(actor, LogAction.UPDATE, AuditLogEntryType.DOMAIN, domain);
//		auditMongoRepository.insert(log);
		return domain;
	}

	@Override
	public void deleteDomain(Account actor, String identifier) throws BusinessException {
		if (!actor.hasSuperAdminRole()) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Only root is authorized to create domains.");
		}
		AbstractDomain domain = findById(identifier);
		if (domain.getDomainType().equals(DomainType.ROOTDOMAIN)) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "No one is authorized to delete root domain.");
		}

		boolean domainHasAccessRules = domainAccessPolicyBusinessService.domainHasPolicyRules(domain);
		if (domainHasAccessRules) {
			String errorMessage = "Cannot delete the domain : "
					+ domain.getLabel() + " (" + domain.getUuid()
					+ "), delete the affiliated rules to do so ...";
			logger.info(errorMessage);
			throw new BusinessException(
					BusinessErrorCode.DOMAIN_HAS_ACCESS_RULES, errorMessage);
		}
		abstractDomainRepository.delete(domain);
		DomainAuditLogEntry log = new DomainAuditLogEntry(actor, LogAction.DELETE, AuditLogEntryType.DOMAIN, domain);
		auditMongoRepository.insert(log);
		// Remove element from its ancestor. It does not need to be updated. Do
		// not know why, implicit update somewhere ?
		if (domain.getParentDomain() != null) {
			for (Iterator<AbstractDomain> iterator = domain.getParentDomain()
					.getSubdomain().iterator(); iterator.hasNext();) {
				AbstractDomain s = iterator.next();
				if (s.getUuid().equals(identifier)) {
					iterator.remove();
					// abstractDomainRepository.update(domain.getParentDomain());
					break;
				}
			}
		}
	}

	@Override
	public List<String> getAllDomainIdentifiers() {
		return abstractDomainRepository.findAllDomainIdentifiers();
	}

	@Override
	public List<String> getAllMyDomainIdentifiers(String personalDomainIdentifer) {
		AbstractDomain domain = domainBusinessService.find(personalDomainIdentifer);
		return domainBusinessService.getAllMyDomainIdentifiers(domain);
	}

	@Override
	public List<AbstractDomain> getAllDomains() {
		return abstractDomainRepository.findAllDomain();
	}

	@Override
	public List<AbstractDomain> getAllTopAndSubDomain() {
		return abstractDomainRepository.findAllTopAndSubDomain();
	}

	@Override
	public List<AbstractDomain> getAllTopDomain() {
		return abstractDomainRepository.findAllTopDomain();
	}

	@Override
	public List<AbstractDomain> getAllSubDomain() {
		return abstractDomainRepository.findAllSubDomain();
	}

	@Override
	public AbstractDomain updateDomain(Account actor, AbstractDomain domain) throws BusinessException {
		Validate.notEmpty(domain.getDescription(), "Description must be set.");
		Validate.notEmpty(domain.getLabel(), "Domain label must be set.");
		if (!actor.hasSuperAdminRole()) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Only root is authorized to create domains.");
		}
		logger.debug("Update domain :" + domain.getUuid());
		if (domain.getUuid() == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_ID_NOT_FOUND,
					"This domain has no current identifier.");
		}
		AbstractDomain entity = findById(domain.getUuid());
		DomainAuditLogEntry log = new DomainAuditLogEntry(actor, LogAction.UPDATE, AuditLogEntryType.DOMAIN, entity);
		if (domain.getPolicy() == null) {
			throw new BusinessException(
					BusinessErrorCode.DOMAIN_POLICY_NOT_FOUND,
					"This domain has no domain policy.");
		}

		if (domain.getCurrentMailConfiguration() == null) {
			throw new BusinessException(BusinessErrorCode.MAILCONFIG_NOT_FOUND,
					"This domain has no mail config.");
		} else {
			MailConfig mailConfig = mailConfigBusinessService.findByUuid(domain
					.getCurrentMailConfiguration().getUuid());
			entity.setCurrentMailConfiguration(mailConfig);
		}

		if (domain.getMimePolicy() == null) {
			throw new BusinessException(BusinessErrorCode.MIME_NOT_FOUND,
					"This domain has no mime policy.");
		} else {
			MimePolicy mimePolicy = mimePolicyBusinessService.find(domain
					.getMimePolicy().getUuid());
			entity.setMimePolicy(mimePolicy);
		}

		DomainPolicy policy = domainPolicyService.find(domain.getPolicy()
				.getUuid());
		if (policy == null) {
			throw new BusinessException(
					BusinessErrorCode.DOMAIN_POLICY_NOT_FOUND,
					"This new domain has a wrong domain policy identifier.");
		}
		entity.setPolicy(policy);
		entity.updateDomainWith(domain);
		if (entity.getDomainType().equals(DomainType.ROOTDOMAIN)) {
			return abstractDomainRepository.update(entity);
		} else if (entity.getDomainType().equals(DomainType.GUESTDOMAIN)) {
			return abstractDomainRepository.update(entity);
		} else {
			// Ugly part ! :(
			if (entity.getUserProvider() == null) {
				entity.setUserProvider(domain.getUserProvider());
				return abstractDomainRepository.update(entity);
			} else {
				if (domain.getUserProvider() == null) {
					UserProvider currentProvider = entity.getUserProvider();
					entity.setUserProvider(null);
					AbstractDomain update = abstractDomainRepository.update(entity);
					userProviderService.delete(currentProvider );
					return update;
				}
			}
		}
		entity = abstractDomainRepository.update(entity);
		log.setResourceUpdated(new DomainMto(entity));
		auditMongoRepository.insert(log);
		return entity;
	}

	@Override
	public User findUserWithoutRestriction(AbstractDomain domain, String mail)
			throws BusinessException {
		User user = null;
		if (domain.getUserProvider() != null) {
			user = userProviderService.findUser(domain.getUserProvider(), mail);
			if (user != null) {
				user.setDomain(domain);
				user.setRole(user.getDomain().getDefaultRole());
			}
		} else {
			logger.debug("UserProvider is null for domain : "
					+ domain.getUuid());
		}
		return user;
	}

	@Override
	public Boolean isUserExist(AbstractDomain domain, String mail)
			throws BusinessException {
		if (domain.getUserProvider() != null) {
			return userProviderService.isUserExist(domain.getUserProvider(),
					mail);
		} else {
			logger.debug("UserProvider is null for domain : "
					+ domain.getUuid());
		}
		return false;
	}

	private List<User> findUserRecursivelyWithoutRestriction(
			AbstractDomain domain, String mail) throws BusinessException {
		List<User> users = new ArrayList<User>();
		try {
			// TODO FMA
			User temp = findUserWithoutRestriction(domain, mail);
			if (temp != null) {
				users.add(temp);
			}
		} catch (BusinessException e) {
			logger.error(e.getMessage());
		}
		for (AbstractDomain subDomain : domain.getSubdomain()) {
			users.addAll(findUserRecursivelyWithoutRestriction(subDomain, mail));
		}
		return users;
	}

	@Override
	public List<User> autoCompleteUserWithDomainPolicies(
			String domainIdentifier, String pattern) throws BusinessException {
		logger.debug("Begin autoCompleteUserWithDomainPolicies");
		List<User> users = new ArrayList<User>();

		AbstractDomain domain = retrieveDomain(domainIdentifier);
		if (domain != null) {
			users.addAll(autoCompleteUserWithDomainPolicies(domain, pattern));
		} else {
			logger.error("Can not find domain : " + domainIdentifier
					+ ". This domain does not exist.");
		}
		logger.debug("End autoCompleteUserWithDomainPolicies");
		return users;
	}

	@Override
	public List<User> autoCompleteUserWithDomainPolicies(
			String domainIdentifier, String firstName, String lastName)
			throws BusinessException {
		logger.debug("Begin autoCompleteUserWithDomainPolicies");
		List<User> users = new ArrayList<User>();

		AbstractDomain domain = retrieveDomain(domainIdentifier);
		if (domain != null) {
			users.addAll(autoCompleteUserWithDomainPolicies(domain, firstName,
					lastName));
		} else {
			logger.error("Can not find domain : " + domainIdentifier
					+ ". This domain does not exist.");
		}
		logger.debug("End autoCompleteUserWithDomainPolicies");
		return users;
	}

	private List<User> autoCompleteUserWithDomainPolicies(
			AbstractDomain domain, String pattern) throws BusinessException {
		List<User> users = new ArrayList<User>();

		List<AbstractDomain> allAuthorizedDomain = domainPolicyService
				.getAllAuthorizedDomain(domain);
		for (AbstractDomain d : allAuthorizedDomain) {

			// if the current domain is linked to a UserProvider, we perform a
			// search.
			if (d.getUserProvider() != null) {
				List<User> list = userProviderService.autoCompleteUser(
						d.getUserProvider(), pattern);
				for (User user : list) {
					user.setDomain(d);
				}
				users.addAll(list);
			} else {
				logger.debug("UserProvider is null for domain : "
						+ domain.getUuid());
			}
		}
		return users;
	}

	private List<User> autoCompleteUserWithDomainPolicies(
			AbstractDomain domain, String firstName, String lastName)
			throws BusinessException {
		List<User> users = new ArrayList<User>();

		List<AbstractDomain> allAuthorizedDomain = domainPolicyService
				.getAllAuthorizedDomain(domain);
		for (AbstractDomain d : allAuthorizedDomain) {

			// if the current domain is linked to a UserProvider, we perform a
			// search.
			if (d.getUserProvider() != null) {
				users.addAll(userProviderService.autoCompleteUser(
						d.getUserProvider(), firstName, lastName));
			} else {
				logger.debug("UserProvider is null for domain : "
						+ domain.getUuid());
			}
		}
		return users;
	}

	@Override
	public List<User> searchUserRecursivelyWithoutRestriction(String mail)
			throws BusinessException {
		List<User> users = new ArrayList<User>();
		users.addAll(findUserRecursivelyWithoutRestriction(
				getUniqueRootDomain(), mail));
		return users;
	}

	@Override
	public User searchOneUserRecursivelyWithoutRestriction(
			String domainIdentifier, String mail) throws BusinessException {
		// search domain
		AbstractDomain domain = retrieveDomain(domainIdentifier);
		if (domain == null) {
			logger.error("Impossible to find an user (ldap entry) from domain : "
					+ domainIdentifier + ". This domain does not exist.");
			return null;
		}
		// search user mail in in specific directory and all its SubDomain
		List<User> users = findUserRecursivelyWithoutRestriction(domain, mail);

		if (users != null) {
			if (users.size() == 1) {
				User userFound = users.get(0);
				logger.debug("User '" + mail + "'found in domain : "
						+ userFound.getDomainId());
				return userFound;
			} else if (users.size() > 1) {
				logger.error("Impossible to find an user entity from domain : "
						+ domainIdentifier + ". Multiple results with mail : "
						+ mail);
			} else if (logger.isDebugEnabled()) {
				logger.error("Impossible to find an user entity from domain : "
						+ domainIdentifier + ". No result with mail : " + mail);
			}
		} else if (logger.isDebugEnabled()) {
			logger.error("Impossible to find an user entity from domain : "
					+ domainIdentifier
					+ ". The searchUserRecursivelyWithoutRestriction method returns null.");
		}
		return null;
	}

	@Override
	public List<User> searchUserRecursivelyWithoutRestriction(
			String domainIdentifier, String mail) throws BusinessException {
		logger.debug("Begin searchUserRecursivelyWithoutRestriction");
		AbstractDomain domain = retrieveDomain(domainIdentifier);
		if (domain == null) {
			logger.error("Impossible to find domain : " + domainIdentifier
					+ ". This domain does not exist.");
			return null;
		}
		List<User> users = findUserRecursivelyWithoutRestriction(domain, mail);
		logger.debug("End searchUserRecursivelyWithoutRestriction");
		return users;
	}

	@Override
	public List<User> searchUserWithDomainPolicies(String domainIdentifier,
			String mail, String firstName, String lastName)
			throws BusinessException {
		logger.debug("Begin searchUserRecursivelyWithDomainPolicies");
		List<User> users = new ArrayList<User>();
		AbstractDomain domain = retrieveDomain(domainIdentifier);
		if (domain != null) {
			users.addAll(searchUserWithDomainPolicies(domain, mail, firstName,
					lastName));
		} else {
			logger.error("Impossible to find domain : " + domainIdentifier
					+ ". This domain does not exist.");
		}
		logger.debug("End searchUserRecursivelyWithDomainPolicies");
		return users;
	}

	private List<User> searchUserWithDomainPolicies(AbstractDomain domain,
			String mail, String firstName, String lastName)
			throws BusinessException {
		List<User> users = new ArrayList<User>();
		List<AbstractDomain> allAuthorizedDomain = domainPolicyService
				.getAllAuthorizedDomain(domain);
		for (AbstractDomain d : allAuthorizedDomain) {
			if (d.getUserProvider() != null) {
				List<User> ldapUserList = new ArrayList<User>();
				try {
					ldapUserList = userProviderService.searchUser(
							d.getUserProvider(), mail, firstName, lastName);
				} catch (BusinessException e) {
					logger.error("can not search users from domain:"
							+ d.getUuid());
				}
				// For each user, we set the domain which he came from.
				for (User ldapUser : ldapUserList) {
					User userDb = userRepository.findByMailAndDomain(
							d.getUuid(), ldapUser.getMail());
					if (userDb != null) {
						users.add(userDb);
					} else {
						// this two attributes must be set in order to let
						// ihm (tapestry) find
						// - if user is admin or not (in the result list)
						// - the domain who came from the user.
						ldapUser.setDomain(d);
						ldapUser.setRole(d.getDefaultRole());
						users.add(ldapUser);
					}
				}
			} else {
				logger.debug("UserProvider is null for domain : "
						+ domain.getUuid());
			}
		}

		return users;
	}

	@Deprecated
	@Override
	public List<AbstractDomain> getAllAuthorizedDomains(String domainIdentifier) throws BusinessException {
		logger.debug("Begin getAllAuthorizedDomains" + domainIdentifier);
		AbstractDomain domain = findById(domainIdentifier);
		List<AbstractDomain> domains = domainPolicyService
				.getAllAuthorizedDomain(domain);
		logger.debug("End getAllAuthorizedDomains");
		return domains;
	}

	@Override
	public List<AbstractDomain> getAllAuthorizedDomains(AbstractDomain domain) throws BusinessException {
		logger.debug("Begin getAllAuthorizedDomains" + domain.getUuid());
		List<AbstractDomain> domains = domainPolicyService.getAllAuthorizedDomain(domain);
		logger.debug("End getAllAuthorizedDomains, size : " + domains.size());
		return domains;
	}

	@Deprecated
	@Override
	public boolean userCanCreateGuest(User user) {

		if (user.getAccountType() == AccountType.GUEST) {
			return false;
		}

		AbstractDomain domain = user.getDomain();
		if (domain != null) {
			Functionality func = functionalityReadOnlyService
					.getGuests(domain);
			if (func.getActivationPolicy().getStatus()) {
				try {
					AbstractDomain guestDomain = domainBusinessService.findGuestDomain(domain);
					if (guestDomain != null) {
						return true;
					}
				} catch (BusinessException e) {
					logger.error("Guest functionality is enable, but no guest domain found for domain : "
							+ domain.getUuid());
					return false;
				}
			} else {
				logger.debug("Guest functionality is disable.");
			}
		} else {
			logger.debug("User (actor) " + user.getMail() + " without domain.");
		}
		return false;
	}

	@Override
	public boolean canCreateGuestDomain(AbstractDomain domain) {
		if (domain != null) {
			// search GuestDomain among subdomains
			if (domain.getSubdomain() != null) {
				for (AbstractDomain d : domain.getSubdomain()) {
					if (d.getDomainType().equals(DomainType.GUESTDOMAIN)) {
						logger.debug("Guest domain already exist.");
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public AbstractDomain findGuestDomain(String uuid) {
		AbstractDomain domain = domainBusinessService.find(uuid);
		if (domain == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_ID_NOT_FOUND,
					"The current domain does not exist : " + uuid);
		}
		return domainBusinessService.findGuestDomain(domain);
	}

	@Override
	public String getDomainMail(AbstractDomain domain) {
		if (domain == null) {
			logger.debug("No Domain found.");
			return null;
		}
		return functionalityReadOnlyService.getDomainMailFunctionality(domain)
				.getValue();
	}

	@Override
	public List<AbstractDomain> findAll(Account actor) {
		if (actor.hasSuperAdminRole()) {
			return abstractDomainRepository.findAll();
		} else {
			List<AbstractDomain> domainList = Lists.newArrayList();
			domainList.add(actor.getDomain());
			Set<AbstractDomain> entities = actor.getDomain().getSubdomain();
			for (AbstractDomain abstractDomain : entities) {
				domainList.add(abstractDomain);
			}
			return domainList;
		}
	}

	@Override
	public List<AbstractDomain> loadRelativeDomains(User actor,
			String uuid) throws BusinessException {
		if (actor.hasAdminRole() || actor.hasSuperAdminRole()) {
			WelcomeMessages welcomeMessage = welcomeMessagesService.find(actor, uuid);
			return domainBusinessService.loadRelativeDomains(welcomeMessage);
		} else
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "actor has no rights");
	}

	@Override
	public List<String> getAllSubDomainIdentifiers(String domain) {
		Validate.notEmpty(domain, "Missing domain identifier.");
		return domainBusinessService.getAllSubDomainIdentifiers(domain);
	}

	@Override
	public List<User> autoCompleteUserWithoutDomainPolicies(
			Account actor, String pattern) throws BusinessException {
		if (!actor.hasAllRights()) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "forbidden");
		}
		List<User> users = new ArrayList<User>();
		for (AbstractDomain d : abstractDomainRepository.findAllDomain()) {
			// if the current domain is linked to a UserProvider, we perform a
			// search.
			if (d.getUserProvider() != null) {
				List<User> list = userProviderService.autoCompleteUser(
						d.getUserProvider(), pattern);
				users.addAll(list);
			} else {
				logger.debug("UserProvider is null for domain : "
						+ d.getUuid());
			}
		}
		return users;
	}

	private void createDomainQuotaAndContainerQuota(AbstractDomain domain) throws BusinessException {
		AbstractDomain parentDomain = domain.getParentDomain();
		boolean isSubdomain = domain.getDomainType().equals(DomainType.SUBDOMAIN);
		// Quota for the new domain
		DomainQuota parentDomainQuota = domainQuotaBusinessService.find(parentDomain);
		DomainQuota domainQuota = new DomainQuota(parentDomainQuota, domain);
		if (isSubdomain) {
			domainQuota.setDefaultQuota(null);
			domainQuota.setDefaultQuotaOverride(null);
		}
		domainQuotaBusinessService.create(domainQuota);
		// Quota containers for the new domain.
		for (ContainerQuota parentContainerQuota : containerQuotaBusinessService.findAll(parentDomain)) {
			ContainerQuota cq = new ContainerQuota(domain, parentDomain, domainQuota, parentContainerQuota);
			if (isSubdomain) {
				cq.setDefaultQuota(null);
				cq.setDefaultQuotaOverride(null);
			}
			containerQuotaBusinessService.create(cq);
		}
	}
}
