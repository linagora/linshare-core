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

import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.MailingListBusinessService;
import org.linagora.linshare.core.domain.constants.Role;
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

		if (actor.isSuperAdmin())
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You are not authorized to create a list.");
		return mailingListBusinessService.createList(list, owner);
	}

	@Override
	public MailingList findByUuid(String uuid) throws BusinessException {
		Validate.notEmpty(uuid);

		MailingList list = mailingListBusinessService.findByUuid(uuid);

		if (list == null)
			throw new BusinessException(BusinessErrorCode.LIST_DO_NOT_EXIST, "List does not exist : " + uuid);
		return list;
	}

	@Override
	public MailingList findByIdentifier(String ownerUuid, String identifier) {
		Validate.notEmpty(ownerUuid);

		User owner = userService.findByLsUuid(ownerUuid);

		return mailingListBusinessService.findByIdentifier(owner, identifier);
	}

	@Override
	public List<String> getAllContactMails(String uuid) throws BusinessException {
		Validate.notEmpty(uuid);

		return mailingListBusinessService.getAllContactMails(findByUuid(uuid));
	}

	@Override
	public List<MailingList> findAllListByUser(String actorUuid) {
		Validate.notEmpty(actorUuid);

		User actor = userService.findByLsUuid(actorUuid);

		return mailingListBusinessService.findAllListByUser(actor);
	}

	@Override
	public List<MailingList> searchListByVisibility(String actorUuid, String criteriaOnSearch, String pattern) {
		Validate.notEmpty(actorUuid);
		Validate.notEmpty(criteriaOnSearch);
		Validate.notEmpty(pattern);

		User actor = userService.findByLsUuid(actorUuid);

		if (criteriaOnSearch.equals(VisibilityType.All.name()))
			return mailingListBusinessService.searchListByUser(actor, pattern);
		if (criteriaOnSearch.equals(VisibilityType.AllMyLists.name()))
			return mailingListBusinessService.searchMyLists(actor, pattern);
		return mailingListBusinessService.searchListByVisibility(actor,
				criteriaOnSearch.equals(VisibilityType.Public.name()), pattern);
	}

	@Override
	public List<MailingList> findAllListByVisibility(String actorUuid, String criteriaOnSearch) {
		Validate.notEmpty(criteriaOnSearch);
		Validate.notEmpty(actorUuid);

		User actor = userService.findByLsUuid(actorUuid);

		if (criteriaOnSearch.equals(VisibilityType.All.name()))
			return mailingListBusinessService.findAllListByUser(actor);
		if (criteriaOnSearch.equals(VisibilityType.AllMyLists.name()))
			return mailingListBusinessService.findAllMyList(actor);
		return mailingListBusinessService.findAllListByVisibility(actor,
				criteriaOnSearch.equals(VisibilityType.Public.name()));
	}

	@Override
	public List<MailingList> findAllListByOwner(String ownerUuid) {
		Validate.notEmpty(ownerUuid);

		User owner = userService.findByLsUuid(ownerUuid);

		return mailingListBusinessService.findAllMyList(owner);
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
	public void updateList(String actorUuid, MailingList listToUpdate) throws BusinessException {
		Validate.notEmpty(actorUuid);
		Validate.notNull(listToUpdate);

		User actor = userService.findByLsUuid(actorUuid);

		if (!actor.isSuperAdmin()) {
			checkRights(actor, listToUpdate, "You are not authorized to update this list.");
		}

		if (actor.isSuperAdmin()) {
			// only super admin is authorized to modify list owner.
			User owner = listToUpdate.getOwner();
			if (owner != null) {
				listToUpdate.setNewOwner(userService.findByLsUuid(owner.getLsUuid()));
			}
		}
		mailingListBusinessService.updateList(listToUpdate);
	}

	/**
	 * Basic operations on mailingListContact
	 */

	@Override
	public void addNewContact(String actorUuid, String mailingListUuid, MailingListContact contact)
			throws BusinessException {
		Validate.notEmpty(actorUuid);
		Validate.notEmpty(mailingListUuid);
		Validate.notNull(contact);

		User actor = userService.findByLsUuid(actorUuid);
		MailingList list = mailingListBusinessService.findByUuid(mailingListUuid);

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
	public void updateContact(String actorUuid, MailingListContact contactToUpdate) throws BusinessException {
		Validate.notNull(actorUuid);
		Validate.notNull(contactToUpdate);

		MailingListContact contact = mailingListBusinessService.findContact(contactToUpdate.getUuid());
		MailingList list = contact.getMailingList();
		User actor = userService.findByLsUuid(actorUuid);

		checkRights(actor, list, "You are not authorized to delete a contact");
		mailingListBusinessService.updateContact(contactToUpdate);
	}

	@Override
	public void deleteContact(String actorUuid, String contactUuid) throws BusinessException {
		Validate.notEmpty(actorUuid);
		Validate.notEmpty(contactUuid);

		User actor = userService.findByLsUuid(actorUuid);
		MailingListContact contact = mailingListBusinessService.findContact(contactUuid);
		MailingList mailingList = contact.getMailingList();

		checkRights(actor, mailingList, "You are not authorized to delete a contact");
		mailingListBusinessService.deleteContact(mailingList, contactUuid);
	}

	private void checkRights(User actor, MailingList list, String msg)
			throws BusinessException {
		if (actor.getRole().equals(Role.SUPERADMIN)
				|| actor.getRole().equals(Role.SYSTEM))
			return;
		MailingList entityList = findByUuid(list.getUuid());
		if (!actor.equals(entityList.getOwner()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, msg);
	}
}
