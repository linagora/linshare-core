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

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.MailingListBusinessService;
import org.linagora.linshare.core.domain.constants.VisibilityType;
import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.domain.entities.MailingListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.MailingListService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * Basic operations on list
	 */

	@Override
	public MailingList createList(String actorUuid, String ownerUuid, MailingList list) throws BusinessException {
		Validate.notEmpty(actorUuid);
		Validate.notEmpty(ownerUuid);
		Validate.notNull(list);

		User actor = userService.findByLsUuid(actorUuid);
		User owner = userService.findByLsUuid(ownerUuid);

		if (!actor.isSuperAdmin()) {
			return mailingListBusinessService.createList(list, owner);
		} else {
			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to create a list.");
		}
	}

	@Override
	public MailingList findByUuid(String uuid) throws BusinessException {
		Validate.notEmpty(uuid);

		MailingList list = mailingListBusinessService.findByUuid(uuid);
		if (list == null)
			throw new BusinessException(BusinessErrorCode.LIST_DO_NOT_EXIST,
					"List does not exist : " + uuid);
		return list;
	}

	@Override
	public MailingList findByIdentifier(String ownerUuid, String identifier) {
		Validate.notEmpty(ownerUuid);

		User owner = userService.findByLsUuid(ownerUuid);

		return mailingListBusinessService.findByIdentifier(owner, identifier);
	}

	@Override
	public List<MailingList> findAllListByUser(String actorUuid) {
		Validate.notEmpty(actorUuid);

		User owner = userService.findByLsUuid(actorUuid);

		return mailingListBusinessService.findAllListByUser(owner);
	}

	@Override
	public List<MailingList> searchListByVisibility(String ownerUuid, String criteriaOnSearch, String pattern) {
		Validate.notEmpty(ownerUuid);
		Validate.notEmpty(criteriaOnSearch);
		Validate.notEmpty(pattern);

		boolean isPublic;
		User actor = userService.findByLsUuid(ownerUuid);

		if (criteriaOnSearch.equals(VisibilityType.All.toString())) {
			return mailingListBusinessService.searchListByUser(actor, pattern);
		} else if (criteriaOnSearch.equals(VisibilityType.AllMyLists.toString())) {
			return mailingListBusinessService.searchMyLists(actor, pattern);
		} else if (criteriaOnSearch.equals(VisibilityType.Public.toString())) {
			isPublic = true;
		} else {
			isPublic = false;
		}
		return mailingListBusinessService.searchListByVisibility(actor, isPublic, pattern);
	}

	@Override
	public List<MailingList> findAllListByVisibility(String actorUuid,
			String criteriaOnSearch) {
		Validate.notEmpty(criteriaOnSearch);
		Validate.notEmpty(actorUuid);

		User actor = userService.findByLsUuid(actorUuid);

		if (criteriaOnSearch.equals(VisibilityType.All))
			return actor.isSuperAdmin() ? mailingListBusinessService
					.findAllList() : mailingListBusinessService
					.findAllListByUser(actor);
		if (criteriaOnSearch.equals(VisibilityType.AllMyLists))
			return mailingListBusinessService.findAllMyList(actor);
		return mailingListBusinessService.findAllListByVisibility(actor,
				criteriaOnSearch.equals(VisibilityType.Public));
	}

	@Override
	public List<MailingList> findAllListByOwner(String ownerUuid) {
		Validate.notEmpty(ownerUuid);

		User actor = userService.findByLsUuid(ownerUuid);

		return mailingListBusinessService.findAllMyList(actor);
	}

	@Override
	public void deleteList(String actorUuid, String mailingListUuid) throws BusinessException {
		Validate.notEmpty(mailingListUuid);
		Validate.notEmpty(actorUuid);

		MailingList list = findByUuid(mailingListUuid);
		User actor = userService.findByLsUuid(actorUuid);

		if (!actor.isSuperAdmin())
			checkRights(actor, list, "You are not authorized to delete this list.");
		mailingListBusinessService.deleteList(mailingListUuid);
	}

	@Override
	public void updateList(String actorUuid, MailingList listToUpdate, String newOwnerUuid)
			throws BusinessException {
		Validate.notEmpty(actorUuid);
		Validate.notNull(listToUpdate);

		User actor = userService.findByLsUuid(actorUuid);
		MailingList list = findByUuid(listToUpdate.getUuid());

		if (!actor.isSuperAdmin())
			checkRights(actor, list, "You are not authorized to update this list.");
		if (newOwnerUuid != null) {
			if (actor.isSuperAdmin()) {
				User owner = userService.findByLsUuid(newOwnerUuid);
				listToUpdate.setNewOwner(owner);
			} else {
				logger.warn("The current user " + actor.getAccountReprentation()
						+ " is trying to update the owner.");
			}
		} else {
			listToUpdate.setOwner(list.getOwner());
		}
		mailingListBusinessService.updateList(listToUpdate);
	}

	/**
	 * Basic operations on mailingListContact
	 */

	@Override
	public void addNewContact(String ownerUuid, String mailingListUuid, MailingListContact contact)
			throws BusinessException {
		Validate.notEmpty(ownerUuid);
		Validate.notEmpty(mailingListUuid);
		Validate.notNull(contact);

		User actor = userService.findByLsUuid(ownerUuid);
		MailingList list = findByUuid(mailingListUuid);

		checkRights(actor, list, "You are not authorized to create a contact");
		mailingListBusinessService.addContact(list, contact);
	}

	@Override
	public MailingListContact searchContact(String uuid) throws BusinessException {
		Validate.notNull(uuid);

		return mailingListBusinessService.findContact(uuid);
	}

	@Override
	public MailingListContact findContactWithMail(String listUuid, String mail) throws BusinessException {
		return mailingListBusinessService.findContactWithMail(listUuid, mail);
	}

	@Override
	public void updateContact(String ownerUuid, String listUuid, MailingListContact contactToUpdate)
			throws BusinessException {
		Validate.notEmpty(listUuid);
		Validate.notEmpty(ownerUuid);
		Validate.notNull(contactToUpdate);

		MailingList list = findByUuid(listUuid);
		User actor = userService.findByLsUuid(ownerUuid);

		checkRights(actor, list, "You are not authorized to delete a contact");
		mailingListBusinessService.updateContact(contactToUpdate);
	}

	@Override
	public void deleteContact(String actorUuid, String listUuid, String contactUuid)
			throws BusinessException {
		Validate.notEmpty(listUuid);
		Validate.notEmpty(actorUuid);
		Validate.notEmpty(contactUuid);

		User actor = userService.findByLsUuid(actorUuid);
		MailingList list = findByUuid(listUuid);

		checkRights(actor, list, "You are not authorized to delete a contact");
		mailingListBusinessService.deleteContact(list, contactUuid);
	}
	
	private void checkRights(User actor, MailingList list, String msg)
			throws BusinessException {
		if (!actor.equals(list.getOwner()))
			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, msg);
	}
}
