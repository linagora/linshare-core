/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2022. Contribute to
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

import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.hibernate.criterion.Order;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.domain.entities.fields.UserFields;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.impl.UserResourceAccessControlImpl;
import org.linagora.linshare.core.repository.AllowedContactRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.service.UserService2;
import org.linagora.linshare.webservice.utils.PageContainer;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class UserService2Impl extends GenericServiceImpl<Account, User> implements UserService2 {

	private final UserRepository<User> userRepository;

	private final AllowedContactRepository allowedContactRepository;

	private final UserService userService;

	private final AbstractDomainService abstractDomainService;

	private DomainPermissionBusinessService permissionService;

	public UserService2Impl(
			UserResourceAccessControlImpl rac,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			UserRepository<User> userRepository,
			UserService userService,
			AbstractDomainService abstractDomainService,
			AllowedContactRepository allowedContactRepository,
			DomainPermissionBusinessService permissionService) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.userRepository = userRepository;
		this.userService = userService;
		this.allowedContactRepository = allowedContactRepository;
		this.abstractDomainService = abstractDomainService;
		this.permissionService = permissionService;
	}

	@Override
	public PageContainer<User> findAll(Account authUser, Account actor, List<String> domainsUuids, SortOrder sortOrder,
			UserFields sortField, String mail, String firstName, String lastName, Boolean restricted,
			Boolean canCreateGuest, Boolean canUpload, String role, String type, PageContainer<User> container) {
		preChecks(authUser, actor);
		List<AbstractDomain> domains = Lists.newArrayList();
		for (String domainUuid : domainsUuids) {
			AbstractDomain domain = abstractDomainService.findById(domainUuid);
			domains.add(domain);
		}
		checkListPermission(authUser, actor, User.class, BusinessErrorCode.USER_FORBIDDEN, null, domains);
		if (actor.hasAdminRole()) {
			if (domains.isEmpty()) {
				domains = permissionService.getMyAdministredDomains(actor);
			}
		}
		Role checkedRole = Strings.isNullOrEmpty(role) ? null : Role.valueOf(role);
		AccountType checkedAccountType = Strings.isNullOrEmpty(type) ? null : AccountType.valueOf(type);
		Order order = checkSortOrderAndField(sortOrder, sortField);
		return userRepository.findAll(domains, order, mail, firstName, lastName, restricted, canCreateGuest, canUpload,
				checkedRole, checkedAccountType, container);
	}

	@Override
	public List<User> autoCompleteUser(Account authUser, Account actor, String patternStr) throws BusinessException {
		preChecks(authUser, actor);
		Pattern pattern = new Pattern(patternStr);
		if (actor.isGuest()) {
			// restricted guests must not see all users.
			Guest guest = (Guest)actor;
			if (guest.isRestricted()) {
				List<AllowedContact> contacts;
				if (pattern.useEmailAsSearchPattern()) {
					contacts = allowedContactRepository.completeContact(guest, pattern.getMail());
				} else {
					contacts = allowedContactRepository.completeContact(guest, pattern.getFirstName(), pattern.getLastName());
				}
				return contacts.stream().map(contact -> contact.getContact()).collect(Collectors.toUnmodifiableList());
			}
		}
		List<AbstractDomain> domains = null;
		if (!actor.isRoot()) {
			domains = abstractDomainService.getAllAuthorizedDomains(actor.getDomain());
		}
		if (pattern.useEmailAsSearchPattern()) {
			return userRepository.autoCompleteUser(domains, pattern.getMail());
		} else {
			return userRepository.autoCompleteUser(domains, pattern.getFirstName(), pattern.getLastName());
		}
	}

	private Order checkSortOrderAndField(SortOrder sortOrder, UserFields sortField) {
		switch (sortField) {
			case accountType:
				return SortOrder.addAccountTypeSortOrder(sortOrder);
			case domain:
				return SortOrder.addDomainSortOrder(sortOrder);
			default:
				return SortOrder.addOrder(sortOrder, sortField);
		}
	}

	@Override
	public User find(Account authUser, Account actor, String lsUuid) {
		preChecks(authUser, actor);
		checkReadPermission(authUser, actor, User.class, BusinessErrorCode.USER_FORBIDDEN, null);
		return userService.findByLsUuid(lsUuid);
	}

	@Override
	public User unlock(Account authUser, Account actor, User accountToUnlock) throws BusinessException {
		preChecks(authUser, actor);
		checkUpdatePermission(authUser, actor, User.class, BusinessErrorCode.USER_FORBIDDEN, null);
		return userService.unlockUser(authUser, accountToUnlock);
	}

	@Override
	public User update(Account authUser, Account actor, User userToUpdate, String domainId)
			throws BusinessException {
		preChecks(authUser, actor);
		checkUpdatePermission(authUser, actor, User.class, BusinessErrorCode.USER_FORBIDDEN, null);
		return userService.updateUser(actor, userToUpdate, domainId);
	}

	@Override
	public User delete(Account authUser, Account actor, String lsUuid) throws BusinessException {
		preChecks(authUser, actor);
		checkDeletePermission(authUser, actor, User.class, BusinessErrorCode.USER_FORBIDDEN, null);
		return userService.deleteUser(actor, lsUuid);
	}

	@Override
	public List<AllowedContact> findAllRestrictedContacts(Account authUser, Account actor, User user, String mail,
			String firstName, String lastName) {
		preChecks(authUser, actor);
		checkListPermission(authUser, actor, User.class, BusinessErrorCode.USER_FORBIDDEN, null);
		if (!user.isRestricted()) {
			logger.info("You can not list the restricted contacts for a not restricted user.");
			return Lists.newArrayList();
		}
		return allowedContactRepository.findAllRestrictedContacts(user, mail, firstName, lastName);
	}

	@Override
	public AllowedContact findRestrictedContact(Account authUser, Account actor, User owner,
			String restrictedContactUuid) {
		preChecks(authUser, actor);
		Validate.notNull(owner, "The owner of the restrictedContact must be set.");
		Validate.notEmpty(restrictedContactUuid, "The restrictedContact's uuid must be set.");
		AllowedContact allowedContact = allowedContactRepository.findRestrictedContact(owner, restrictedContactUuid);
		if (Objects.isNull(allowedContact)) {
			throw new BusinessException(BusinessErrorCode.RESTRICTED_CONTACT_NOT_FOUND,
					"The restricted contact with uuid : " + restrictedContactUuid + " is not found.");
		}
		checkReadPermission(authUser, actor, User.class, BusinessErrorCode.USER_FORBIDDEN, allowedContact.getContact());
		return allowedContact;
	}

	@Override
	public AllowedContact createRestrictedContact(Account authUser, Account actor,
			AllowedContact restrictedContactToCreate) {
		preChecks(authUser, actor);
		Validate.notNull(restrictedContactToCreate, "RestrictedContact to create must be set");
		Validate.notNull(restrictedContactToCreate.getOwner(), "RestrictedContact's owner must be set");
		Validate.notNull(restrictedContactToCreate.getContact(), "RestrictedContact's contact must be set");
		checkCreatePermission(authUser, actor, User.class, BusinessErrorCode.USER_FORBIDDEN,
				restrictedContactToCreate.getContact());
		if (findAllRestrictedContacts(authUser, actor, restrictedContactToCreate.getOwner(), null, null, null)
				.contains(restrictedContactToCreate)) {
			return restrictedContactToCreate;
		}
		return allowedContactRepository.create(restrictedContactToCreate);
	}

	@Override
	public AllowedContact deleteRestrictedContact(Account authUser, Account actor, User owner,
			String restrictedContactUuid) {
		preChecks(authUser, actor);
		Validate.notNull(owner, "The owner of the restrictedContact must be set.");
		Validate.notEmpty(restrictedContactUuid, "The restrictedContact's uuid must be set.");
		AllowedContact allowedContact = allowedContactRepository.findRestrictedContact(owner, restrictedContactUuid);
		checkDeletePermission(authUser, actor, User.class, BusinessErrorCode.USER_FORBIDDEN, allowedContact.getContact());
		allowedContactRepository.delete(allowedContact);
		return allowedContact;
	}

	private class Pattern {
		final String pattern;
		String mail;
		String firstName;
		String lastName;

		public Pattern(String pattern) {
			super();
			this.pattern = StringUtils.trim(pattern);
			StringTokenizer stringTokenizer = new StringTokenizer(pattern, " ");
			if (stringTokenizer.countTokens() <= 1) {
				this.mail = pattern;
			} else {
				if (stringTokenizer.hasMoreTokens()) {
					firstName = stringTokenizer.nextToken();
					if (stringTokenizer.hasMoreTokens()) {
						lastName = stringTokenizer.nextToken();
					}
				}
			}
		}

		public boolean useEmailAsSearchPattern() {
			if (lastName == null) {
				return true;
			}
			return false;
		}

		public String getMail() {
			return mail;
		}

		public String getFirstName() {
			return firstName;
		}

		public String getLastName() {
			return lastName;
		}

		@SuppressWarnings("unused")
		public String getPattern() {
			return pattern;
		}

		@Override
		public String toString() {
			return "Pattern [pattern=" + pattern + ", mail=" + mail + ", firstName=" + firstName + ", lastName="
					+ lastName + ", useEmailAsSearchPattern()=" + useEmailAsSearchPattern() + "]";
		}
	}
}
