/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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

import org.linagora.linshare.core.business.service.MailingListBusinessService;
import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.domain.entities.MailingListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.MailingListService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class MailingListServiceImpl implements MailingListService {

	private static final Logger logger = LoggerFactory.getLogger(MailingListServiceImpl.class);

	private final MailingListBusinessService mailingListBusinessService;

	private final UserService userService;

	public MailingListServiceImpl(MailingListBusinessService mailingListBusinessService, UserService userService) {
		super();
		this.mailingListBusinessService = mailingListBusinessService;
		this.userService = userService;
	}

	/**
	 * Basic operations on mailingList
	 */

	@Override
	public MailingList createList(String actorUuid, String ownerUuid, MailingList mailingList) throws BusinessException {
		Assert.notNull(actorUuid);
		Assert.notNull(ownerUuid);
		Assert.notNull(mailingList);

		User actor = userService.findByLsUuid(actorUuid);
		User owner = userService.findByLsUuid(ownerUuid);

		if (!actor.isSuperAdmin()) {
			return mailingListBusinessService.createList(mailingList, owner);
		} else {
			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to create a list.");
		}
	}

	@Override
	public MailingList searchList(String uuid) throws BusinessException {
		Assert.notNull(uuid);

		return mailingListBusinessService.findByUuid(uuid);
	}

	@Override
	public MailingList findByIdentifier(String ownerUuid, String identifier) {
		Assert.notNull(ownerUuid);

		User owner = userService.findByLsUuid(ownerUuid);
		return mailingListBusinessService.findByIdentifier(owner, identifier);
	}

	@Override
	public List<MailingList> findAllListByUser(String ownerUuid) {
		Assert.notNull(ownerUuid);

		User owner = userService.findByLsUuid(ownerUuid);
		return mailingListBusinessService.findAllListByUser(owner);
	}

	@Override
	public List<MailingList> searchListByVisibility(String ownerUuid, String criteriaOnSearch, String pattern) {
		Assert.notNull(ownerUuid);
		Assert.notNull(criteriaOnSearch);
		Assert.notNull(pattern);

		boolean isPublic;
		User actor = userService.findByLsUuid(ownerUuid);

		if (criteriaOnSearch.equals("all")) {
			return mailingListBusinessService.searchListByUser(actor, pattern);
		} else if (criteriaOnSearch.equals("allMyLists")) {
			return mailingListBusinessService.searchMyLists(actor, pattern);
		} else if (criteriaOnSearch.equals("public")) {
			isPublic = true;
		} else {
			isPublic = false;
		}
		return mailingListBusinessService.searchListByVisibility(actor, isPublic, pattern);
	}

	@Override
	public List<MailingList> findAllListByVisibility(String ownerUuid, String criteriaOnSearch) {
		Assert.notNull(criteriaOnSearch);
		Assert.notNull(ownerUuid);

		boolean isPublic;
		User user = userService.findByLsUuid(ownerUuid);

		if (criteriaOnSearch.equals("all")) {
			if (user.isSuperAdmin()) {
				return mailingListBusinessService.findAllList();
			} else {
				return mailingListBusinessService.findAllListByUser(user);
			}
		} else if (criteriaOnSearch.equals("allMyLists")) {
			return mailingListBusinessService.findAllMyList(user);
		} else if (criteriaOnSearch.equals("public")) {
			isPublic = true;
		} else {
			isPublic = false;
		}
		return mailingListBusinessService.findAllListByVisibility(user, isPublic);
	}

	@Override
	public List<MailingList> findAllListByOwner(String ownerUuid) {
		Assert.notNull(ownerUuid);

		User actor = userService.findByLsUuid(ownerUuid);
		return mailingListBusinessService.findAllMyList(actor);
	}

	@Override
	public void deleteList(String actorUuid, String mailingListUuid) throws BusinessException {
		Assert.notNull(mailingListUuid);
		Assert.notNull(actorUuid);

		MailingList mailingList = mailingListBusinessService.findByUuid(mailingListUuid);
		String ownerUuid = mailingList.getOwner().getLsUuid();
		User actor = userService.findByLsUuid(actorUuid);

		if (actor.isSuperAdmin() || actor.getLsUuid().equals(ownerUuid)) {
			mailingListBusinessService.deleteList(mailingListUuid);
		} else {
			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to delete this list.");
		}

	}

	@Override
	public void updateList(String actorUuid, MailingList listToUpdate) throws BusinessException {
		this.updateList(actorUuid, listToUpdate, null);
	}

	@Override
	public void updateList(String actorUuid, MailingList listToUpdate, String newOwnerUuid) throws BusinessException {
		Assert.notNull(actorUuid);
		Assert.notNull(listToUpdate);

		User actor = userService.findByLsUuid(actorUuid);
		MailingList mailingList = mailingListBusinessService.findByUuid(listToUpdate.getUuid());

		if (actor.isSuperAdmin() || actor.getLsUuid().equals(mailingList.getOwner().getLsUuid())) {
			if (newOwnerUuid != null) {
				if (actor.isSuperAdmin()) {
					User owner = userService.findByLsUuid(newOwnerUuid);
					listToUpdate.setNewOwner(owner);
					listToUpdate.setDomain(owner.getDomain());
				} else {
					logger.warn("The current user " + actor.getAccountReprentation()
							+ " is trying to update the owner.");
				}
			}
			listToUpdate.setOwner(mailingList.getOwner());
			listToUpdate.setDomain(mailingList.getOwner().getDomain());
			mailingListBusinessService.updateList(listToUpdate);
		} else {
			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to update this list.");
		}
	}

	/**
	 * Basic operations on mailingListContact
	 */

	@Override
	public void addNewContact(String ownerUuid, String mailingListUuid, MailingListContact contact)
			throws BusinessException {
		Assert.notNull(ownerUuid);
		Assert.notNull(mailingListUuid);
		Assert.notNull(contact);

		User actorEntity = userService.findByLsUuid(ownerUuid);
		MailingList mailingList = mailingListBusinessService.findByUuid(mailingListUuid);
		if (mailingList.isOwner(actorEntity)) {
			mailingListBusinessService.addContact(mailingList, contact);
		} else {
			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to create a contact");
		}
	}

	@Override
	public MailingListContact searchContact(MailingList mailingList, String mail) throws BusinessException {
		Assert.notNull(mail);

		return mailingListBusinessService.findContact(mailingList, mail);
	}

	@Override
	public void updateContact(String ownerUuid, MailingList list, MailingListContact contactToUpdate)
			throws BusinessException {
		Assert.notNull(list);
		Assert.notNull(ownerUuid);
		Assert.notNull(contactToUpdate);

		User actor = userService.findByLsUuid(ownerUuid);
		if (actor.getLsUuid().equals(list.getOwner().getLsUuid())) {
			mailingListBusinessService.updateContact(list, contactToUpdate);
		} else {
			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to delete a contact");
		}
	}

	@Override
	public void deleteContact(String ownerUuid, String listUuid, String mail) throws BusinessException {
		Assert.notNull(listUuid);
		Assert.notNull(ownerUuid);

		User actor = userService.findByLsUuid(ownerUuid);
		MailingList mailingList = mailingListBusinessService.findByUuid(listUuid);
		if (mailingList == null) {
			String msg = "The current mailing list do not exist : " + listUuid;
			logger.error(msg);
			throw new BusinessException(BusinessErrorCode.LIST_DO_NOT_EXIST, msg);
		}
		if (actor.getLsUuid().equals(mailingList.getOwner().getLsUuid())) {
			mailingListBusinessService.deleteContact(mailingList, mail);
		} else {
			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to delete a contact");
		}
	}
}
