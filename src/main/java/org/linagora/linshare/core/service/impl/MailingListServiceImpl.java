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

import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.MailingListBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.constants.VisibilityType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.domain.entities.MailingListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.MailingListResourceAccessControl;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.MailingListService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.logs.MailingListAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.MailingListContactAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.MailingListContactMto;
import org.linagora.linshare.mongo.entities.mto.MailingListMto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailingListServiceImpl extends GenericServiceImpl<Account, MailingList> implements MailingListService {

	private static final Logger logger = LoggerFactory.getLogger(MailingListServiceImpl.class);

	private final MailingListBusinessService mailingListBusinessService;

	private final UserService userService;

	private final LogEntryService logEntryService;

	public MailingListServiceImpl(MailingListBusinessService mailingListBusinessService, UserService userService,
			final LogEntryService logEntryService, MailingListResourceAccessControl rac
			) {
		super(rac);
		this.mailingListBusinessService = mailingListBusinessService;
		this.userService = userService;
		this.logEntryService = logEntryService;
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
		if (actor.hasSuperAdminRole())
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You are not authorized to create a list.");
		MailingList res = mailingListBusinessService.createList(list, owner);
		MailingListAuditLogEntry log = new MailingListAuditLogEntry(new AccountMto(actor), new AccountMto(owner),
				LogAction.CREATE, AuditLogEntryType.CONTACTS_LISTS, res);
		logEntryService.insert(log);
		return res;
	}

	@Override
	public MailingList findByUuid(String actorUuid, String uuid) throws BusinessException {
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
	public List<String> getAllContactMails(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid);
		return mailingListBusinessService.getAllContactMails(findByUuid(actorUuid, uuid));
	}

	@Override
	public List<MailingList> findAllListByUser(String actorUuid, String userUuid) {
		Validate.notEmpty(actorUuid);
		User user = userService.findByLsUuid(userUuid);
		return mailingListBusinessService.findAllListByUser(user);
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
	public List<MailingList> findAllListByOwner(String actorUuid, String ownerUuid) {
		Validate.notEmpty(ownerUuid);

		User owner = userService.findByLsUuid(ownerUuid);
		return mailingListBusinessService.findAllMyList(owner);
	}

	@Override
	public MailingList deleteList(String actorUuid, String mailingListUuid) throws BusinessException {
		Validate.notEmpty(mailingListUuid);
		Validate.notEmpty(actorUuid);

		MailingList list = findByUuid(actorUuid, mailingListUuid);
		User actor = userService.findByLsUuid(actorUuid);
		if (!actor.hasSuperAdminRole())
			checkRights(actor, list, "You are not authorized to delete this list.");
		mailingListBusinessService.deleteList(mailingListUuid);
		MailingListAuditLogEntry log = new MailingListAuditLogEntry(new AccountMto(actor),
				new AccountMto(list.getOwner()), LogAction.DELETE, AuditLogEntryType.CONTACTS_LISTS, list);
		logEntryService.insert(log);
		return list;
	}

	@Override
	public MailingList updateList(String actorUuid, MailingList listToUpdate) throws BusinessException {
		Validate.notEmpty(actorUuid);
		Validate.notNull(listToUpdate);
		Validate.notEmpty(listToUpdate.getUuid());

		User actor = userService.findByLsUuid(actorUuid);
		if (!actor.hasSuperAdminRole()) {
			checkRights(actor, listToUpdate, "You are not authorized to update this list.");
		}
		if (actor.hasSuperAdminRole()) {
			// only super admin is authorized to modify list owner.
			User owner = listToUpdate.getOwner();
			if (owner != null) {
				listToUpdate.setNewOwner(userService.findByLsUuid(owner.getLsUuid()));
			}
		}
		MailingListAuditLogEntry log = new MailingListAuditLogEntry(new AccountMto(actor),
				new AccountMto(listToUpdate.getOwner()), LogAction.UPDATE, AuditLogEntryType.CONTACTS_LISTS, listToUpdate);
		MailingList res = mailingListBusinessService.updateList(listToUpdate);
		log.setResourceUpdated(new MailingListMto(res));
		logEntryService.insert(log);
		return res;
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
		MailingListContactAuditLogEntry log = new MailingListContactAuditLogEntry(new AccountMto(actor),
				new AccountMto(list.getOwner()), LogAction.CREATE, AuditLogEntryType.CONTACTS_LISTS_CONTACTS, list,
				contact);
		logEntryService.insert(log);
	}

	@Override
	public MailingListContact searchContact(String actorUuid, String uuid) throws BusinessException {
		Validate.notNull(uuid);
		return mailingListBusinessService.findContact(uuid);
	}

	@Override
	public MailingListContact findContactWithMail(String actorUuid, String listUuid, String mail)
			throws BusinessException {
		return mailingListBusinessService.findContactWithMail(listUuid, mail);
	}

	@Override
	public void updateContact(String actorUuid, MailingListContact contactToUpdate) throws BusinessException {
		Validate.notNull(actorUuid);
		Validate.notNull(contactToUpdate);

		MailingListContact contact = mailingListBusinessService.findContact(contactToUpdate.getUuid());
		MailingList list = contact.getMailingList();
		User actor = userService.findByLsUuid(actorUuid);
		MailingListContactAuditLogEntry log = new MailingListContactAuditLogEntry(new AccountMto(actor),
				new AccountMto(list.getOwner()), LogAction.UPDATE, AuditLogEntryType.CONTACTS_LISTS_CONTACTS, list,
				contact);
		checkRights(actor, list, "You are not authorized to delete a contact");
		mailingListBusinessService.updateContact(contactToUpdate);
		contact = mailingListBusinessService.findContact(contactToUpdate.getUuid());
		log.setResourceUpdated(new MailingListContactMto(contact));
		logEntryService.insert(log);
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
		MailingListContactAuditLogEntry log = new MailingListContactAuditLogEntry(new AccountMto(actor),
				new AccountMto(mailingList.getOwner()), LogAction.DELETE, AuditLogEntryType.CONTACTS_LISTS_CONTACTS, mailingList,
				contact);
		logEntryService.insert(log);
	}

	private void checkRights(User actor, MailingList list, String msg) throws BusinessException {
		if (actor.getRole().equals(Role.SUPERADMIN) || actor.getRole().equals(Role.SYSTEM))
			return;
		MailingList entityList = findByUuid(actor.getLsUuid(), list.getUuid());
		if (!actor.equals(entityList.getOwner()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, msg);
	}

	/*
	 * Webservice methods
	 */

	@Override
	public List<MailingList> findAllByUser(Account actor, Account owner) throws BusinessException {
		preChecks(actor, owner);
		checkListPermission(actor, owner, MailingList.class, BusinessErrorCode.FORBIDDEN, null);
		return mailingListBusinessService.findAllListByUser((User) owner);
	}

	@Override
	public MailingList find(Account actor, Account owner, String uuid) throws BusinessException {
		preChecks(actor, owner);
		MailingList list = mailingListBusinessService.findByUuid(uuid);
		checkReadPermission(actor, owner, MailingList.class, BusinessErrorCode.FORBIDDEN, list);
		return list;
	}

	@Override
	public MailingList create(Account actor, Account owner, MailingList list) throws BusinessException {
		Validate.notNull(list, "Mailing list must be set.");
		preChecks(actor, owner);

		checkCreatePermission(actor, owner, MailingList.class, BusinessErrorCode.FORBIDDEN, null);
		MailingList listCreated = mailingListBusinessService.createList(list, (User) owner);
		MailingListAuditLogEntry log = new MailingListAuditLogEntry(new AccountMto(actor),
				new AccountMto(list.getOwner()), LogAction.CREATE, AuditLogEntryType.CONTACTS_LISTS, listCreated);
		logEntryService.insert(log);
		return listCreated;
	}

	@Override
	public MailingList update(Account actor, Account owner, MailingList list) throws BusinessException {
		Validate.notNull(list, "Mailing list must be set.");
		Validate.notEmpty(list.getUuid(), "Mailing list uuid must be set.");

		MailingList listToUpdate = find(actor, owner, list.getUuid());
		MailingListAuditLogEntry log = new MailingListAuditLogEntry(new AccountMto(actor),
				new AccountMto(listToUpdate.getOwner()), LogAction.UPDATE, AuditLogEntryType.CONTACTS_LISTS, listToUpdate);
		checkUpdatePermission(actor, owner, MailingList.class, BusinessErrorCode.FORBIDDEN, listToUpdate);
		if (actor.hasSuperAdminRole()) {
			// only super admin is authorized to modify list owner.
			User listOwner = list.getOwner();
			if (listOwner != null) {
				listOwner = userService.findByLsUuid(listOwner.getLsUuid());
				listToUpdate.setNewOwner(listOwner);
			}
		}
		listToUpdate = mailingListBusinessService.update(listToUpdate, list);
		log.setResourceUpdated(new MailingListMto(listToUpdate));
		logEntryService.insert(log);
		return listToUpdate;
	}

	@Override
	public MailingList delete(Account actor, Account owner, String uuid) throws BusinessException {
		Validate.notNull(uuid, "Mailing list must be set.");
		Validate.notEmpty(uuid, "Mailing list uuid must be set.");

		MailingList listToDelete = find(actor, owner, uuid);
		checkDeletePermission(actor, owner, MailingList.class, BusinessErrorCode.FORBIDDEN, listToDelete);
		mailingListBusinessService.delete(listToDelete);
		MailingListAuditLogEntry log = new MailingListAuditLogEntry(new AccountMto(actor),
				new AccountMto(listToDelete.getOwner()), LogAction.DELETE, AuditLogEntryType.CONTACTS_LISTS, listToDelete);
		logEntryService.insert(log);
		return listToDelete;
	}

	@Override
	public MailingListContact addContact(Account actor, Account owner, String listUuid, MailingListContact contact)
			throws BusinessException {
		Validate.notNull(contact, "Contact list must be set.");
		Validate.notEmpty(listUuid, "Mailing list uuid must be set.");

		MailingList listToUpdate = find(actor, owner, listUuid);
		checkUpdatePermission(actor, owner, MailingList.class, BusinessErrorCode.FORBIDDEN, listToUpdate);
		contact = mailingListBusinessService.addContact(listToUpdate, contact);
		MailingListContactAuditLogEntry log = new MailingListContactAuditLogEntry(new AccountMto(actor),
				new AccountMto(listToUpdate.getOwner()), LogAction.CREATE, AuditLogEntryType.CONTACTS_LISTS_CONTACTS, listToUpdate, contact);
		logEntryService.insert(log);
		return contact;
	}

	@Override
	public void updateContact(Account actor, Account owner, MailingListContact contact) throws BusinessException {
		preChecks(actor, owner);
		Validate.notNull(contact, "Contact must be set.");
		Validate.notEmpty(contact.getUuid(), "Contact uuid must be set.");

		MailingListContact contactToUpdate = mailingListBusinessService.findContact(contact.getUuid());
		MailingList list = contactToUpdate.getMailingList();
		MailingListContactAuditLogEntry log = new MailingListContactAuditLogEntry(new AccountMto(actor),
				new AccountMto(list.getOwner()), LogAction.UPDATE, AuditLogEntryType.CONTACTS_LISTS_CONTACTS, list, contactToUpdate);
		checkUpdatePermission(actor, owner, MailingList.class, BusinessErrorCode.FORBIDDEN, list);
		mailingListBusinessService.updateContact(contact);
		contactToUpdate = mailingListBusinessService.findContact(contactToUpdate.getUuid());
		log.setResourceUpdated(new MailingListContactMto(contactToUpdate));
		logEntryService.insert(log);
	}

	@Override
	public void deleteContact(Account actor, Account owner, String contactUuid) throws BusinessException {
		preChecks(actor, owner);
		Validate.notEmpty(contactUuid, "Contact uuid must be set.");

		MailingListContact contactToDelete = mailingListBusinessService.findContact(contactUuid);
		MailingList list = contactToDelete.getMailingList();
		checkUpdatePermission(actor, owner, MailingList.class, BusinessErrorCode.FORBIDDEN, list);
		mailingListBusinessService.deleteContact(list, contactToDelete.getUuid());
		MailingListContactAuditLogEntry log = new MailingListContactAuditLogEntry(new AccountMto(actor),
				new AccountMto(list.getOwner()), LogAction.DELETE, AuditLogEntryType.CONTACTS_LISTS_CONTACTS, list, contactToDelete);
		logEntryService.insert(log);
	}

	@Override
	public List<MailingListContact> findAllContacts(Account actor, Account owner, String listUuid)
			throws BusinessException {
		MailingList list = find(actor, owner, listUuid);
		return mailingListBusinessService.findAllContacts(list);
	}

	@Override
	public List<MailingList> findAll(Account actor, User owner, Boolean mine) {
		List<MailingList> all = null;
		checkListPermission(actor, owner, MailingList.class, BusinessErrorCode.FORBIDDEN, null);
		if (mine == null) {
			all = mailingListBusinessService.findAll(actor, owner);
		} else if (mine) {
			all = mailingListBusinessService.findAllMine(actor, owner);
		} else {
			all = mailingListBusinessService.findAllOthers(actor, owner);
		}
		return all;
	}
}
