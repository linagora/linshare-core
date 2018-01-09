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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.MailingListBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.constants.VisibilityType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ContactListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.MailingListResourceAccessControl;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.ContactListService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.logs.MailingListAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.MailingListContactAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.MailingListContactMto;
import org.linagora.linshare.mongo.entities.mto.MailingListMto;

public class ContactListServiceImpl extends GenericServiceImpl<Account, ContactList> implements ContactListService {

	private final MailingListBusinessService contactListBusinessService;

	private final UserService userService;

	private final LogEntryService logEntryService;

	public ContactListServiceImpl(MailingListBusinessService contactListBusinessService, UserService userService,
			final LogEntryService logEntryService, MailingListResourceAccessControl rac) {
		super(rac);
		this.contactListBusinessService = contactListBusinessService;
		this.userService = userService;
		this.logEntryService = logEntryService;
	}

	/**
	 * Basic operations on list
	 */

	@Override
	public ContactList createList(String actorUuid, String ownerUuid, ContactList list) throws BusinessException {
		Validate.notEmpty(actorUuid);
		Validate.notEmpty(ownerUuid);
		Validate.notNull(list);
		User actor = userService.findByLsUuid(actorUuid);
		User owner = userService.findByLsUuid(ownerUuid);
		if (actor.hasSuperAdminRole())
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You are not authorized to create a list.");
		ContactList res = contactListBusinessService.createList(list, owner);
		MailingListAuditLogEntry log = new MailingListAuditLogEntry(new AccountMto(actor), new AccountMto(owner),
				LogAction.CREATE, AuditLogEntryType.CONTACTS_LISTS, res);
		logEntryService.insert(log);
		return res;
	}

	@Override
	public ContactList findByUuid(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid);
		ContactList list = contactListBusinessService.findByUuid(uuid);
		if (list == null)
			throw new BusinessException(BusinessErrorCode.LIST_DO_NOT_EXIST, "List does not exist : " + uuid);
		return list;
	}

	@Override
	public ContactList findByIdentifier(String ownerUuid, String identifier) {
		Validate.notEmpty(ownerUuid);
		User owner = userService.findByLsUuid(ownerUuid);
		return contactListBusinessService.findByIdentifier(owner, identifier);
	}

	@Override
	public List<String> getAllContactMails(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid);
		return contactListBusinessService.getAllContactMails(findByUuid(actorUuid, uuid));
	}

	@Override
	public List<ContactList> findAllListByUser(String actorUuid, String userUuid) {
		Validate.notEmpty(actorUuid);
		User user = userService.findByLsUuid(userUuid);
		return contactListBusinessService.findAllListByUser(user);
	}

	@Override
	public List<ContactList> searchListByVisibility(String actorUuid, String criteriaOnSearch, String pattern) {
		Validate.notEmpty(actorUuid);
		Validate.notEmpty(criteriaOnSearch);
		Validate.notEmpty(pattern);

		User actor = userService.findByLsUuid(actorUuid);
		if (criteriaOnSearch.equals(VisibilityType.All.name()))
			return contactListBusinessService.searchListByUser(actor, pattern);
		if (criteriaOnSearch.equals(VisibilityType.AllMyLists.name()))
			return contactListBusinessService.searchMyLists(actor, pattern);
		return contactListBusinessService.searchListByVisibility(actor,
				criteriaOnSearch.equals(VisibilityType.Public.name()), pattern);
	}

	@Override
	public List<ContactList> findAllListByOwner(String actorUuid, String ownerUuid) {
		Validate.notEmpty(ownerUuid);

		User owner = userService.findByLsUuid(ownerUuid);
		return contactListBusinessService.findAllMyList(owner);
	}

	@Override
	public ContactList deleteList(String actorUuid, String contactListUuid) throws BusinessException {
		Validate.notEmpty(contactListUuid);
		Validate.notEmpty(actorUuid);

		ContactList list = findByUuid(actorUuid, contactListUuid);
		User actor = userService.findByLsUuid(actorUuid);
		if (!actor.hasSuperAdminRole())
			checkRights(actor, list, "You are not authorized to delete this list.");
		contactListBusinessService.deleteList(contactListUuid);
		MailingListAuditLogEntry log = new MailingListAuditLogEntry(new AccountMto(actor),
				new AccountMto(list.getOwner()), LogAction.DELETE, AuditLogEntryType.CONTACTS_LISTS, list);
		logEntryService.insert(log);
		return list;
	}

	@Override
	public ContactList updateList(String actorUuid, ContactList listToUpdate) throws BusinessException {
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
				new AccountMto(listToUpdate.getOwner()), LogAction.UPDATE, AuditLogEntryType.CONTACTS_LISTS,
				listToUpdate);
		ContactList res = contactListBusinessService.updateList(listToUpdate);
		log.setResourceUpdated(new MailingListMto(res));
		logEntryService.insert(log);
		return res;
	}

	/**
	 * Basic operations on ContactListMember
	 */

	@Override
	public void addNewContact(String actorUuid, String contactListUuid, ContactListContact contact)
			throws BusinessException {
		Validate.notEmpty(actorUuid);
		Validate.notEmpty(contactListUuid);
		Validate.notNull(contact);
		User actor = userService.findByLsUuid(actorUuid);
		ContactList list = contactListBusinessService.findByUuid(contactListUuid);
		checkRights(actor, list, "You are not authorized to create a contact");
		contactListBusinessService.addContact(list, contact);
		MailingListContactAuditLogEntry log = new MailingListContactAuditLogEntry(new AccountMto(actor),
				new AccountMto(list.getOwner()), LogAction.CREATE, AuditLogEntryType.CONTACTS_LISTS_CONTACTS, list,
				contact);
		logEntryService.insert(log);
	}

	@Override
	public ContactListContact searchContact(String actorUuid, String uuid) throws BusinessException {
		Validate.notNull(uuid);
		return contactListBusinessService.findContact(uuid);
	}

	@Override
	public ContactListContact findContactWithMail(String actorUuid, String listUuid, String mail)
			throws BusinessException {
		return contactListBusinessService.findContactWithMail(listUuid, mail);
	}

	@Override
	public void updateContact(String actorUuid, ContactListContact contactToUpdate) throws BusinessException {
		Validate.notNull(actorUuid);
		Validate.notNull(contactToUpdate);

		ContactListContact contact = contactListBusinessService.findContact(contactToUpdate.getUuid());
		ContactList list = contact.getMailingList();
		User actor = userService.findByLsUuid(actorUuid);
		MailingListContactAuditLogEntry log = new MailingListContactAuditLogEntry(new AccountMto(actor),
				new AccountMto(list.getOwner()), LogAction.UPDATE, AuditLogEntryType.CONTACTS_LISTS_CONTACTS, list,
				contact);
		checkRights(actor, list, "You are not authorized to delete a contact");
		contactListBusinessService.updateContact(contactToUpdate);
		contact = contactListBusinessService.findContact(contactToUpdate.getUuid());
		log.setResourceUpdated(new MailingListContactMto(contact));
		logEntryService.insert(log);
	}

	@Override
	public void deleteContact(String actorUuid, String contactUuid) throws BusinessException {
		Validate.notEmpty(actorUuid);
		Validate.notEmpty(contactUuid);

		User actor = userService.findByLsUuid(actorUuid);
		ContactListContact contact = contactListBusinessService.findContact(contactUuid);
		ContactList contactList = contact.getMailingList();
		checkRights(actor, contactList, "You are not authorized to delete a contact");
		contactListBusinessService.deleteContact(contactList, contactUuid);
		MailingListContactAuditLogEntry log = new MailingListContactAuditLogEntry(new AccountMto(actor),
				new AccountMto(contactList.getOwner()), LogAction.DELETE, AuditLogEntryType.CONTACTS_LISTS_CONTACTS,
				contactList, contact);
		logEntryService.insert(log);
	}

	private void checkRights(User actor, ContactList list, String msg) throws BusinessException {
		if (actor.getRole().equals(Role.SUPERADMIN) || actor.getRole().equals(Role.SYSTEM))
			return;
		ContactList entityList = findByUuid(actor.getLsUuid(), list.getUuid());
		if (!actor.equals(entityList.getOwner()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, msg);
	}

	/*
	 * Webservice methods
	 */

	@Override
	public List<ContactList> findAllByUser(Account actor, Account owner) throws BusinessException {
		preChecks(actor, owner);
		checkListPermission(actor, owner, ContactList.class, BusinessErrorCode.FORBIDDEN, null);
		return contactListBusinessService.findAllListByUser((User) owner);
	}

	@Override
	public ContactList find(Account actor, Account owner, String uuid) throws BusinessException {
		preChecks(actor, owner);
		ContactList list = contactListBusinessService.findByUuid(uuid);
		checkReadPermission(actor, owner, ContactList.class, BusinessErrorCode.FORBIDDEN, list);
		return list;
	}

	@Override
	public ContactList create(Account actor, Account owner, ContactList list) throws BusinessException {
		Validate.notNull(list, "Contact list must be set.");
		preChecks(actor, owner);

		checkCreatePermission(actor, owner, ContactList.class, BusinessErrorCode.FORBIDDEN, null);
		ContactList listCreated = contactListBusinessService.createList(list, (User) owner);
		MailingListAuditLogEntry log = new MailingListAuditLogEntry(new AccountMto(actor),
				new AccountMto(list.getOwner()), LogAction.CREATE, AuditLogEntryType.CONTACTS_LISTS, listCreated);
		logEntryService.insert(log);
		return listCreated;
	}

	@Override
	public ContactList duplicate(Account actor, Account owner, ContactList list, String identifier) throws BusinessException {
		Validate.notNull(list, "Mailing list must be set.");
		Validate.notNull(identifier, "identifier must be set.");
		preChecks(actor, owner);
		checkCreatePermission(actor, owner, ContactList.class, BusinessErrorCode.FORBIDDEN, null);
		ContactList duplicateMailingList = new ContactList();
		duplicateMailingList.setIdentifier(identifier);
		duplicateMailingList.setOwner(list.getOwner());
		duplicateMailingList.setDomain(list.getDomain());
		duplicateMailingList.setPublic(list.isPublic());
		duplicateMailingList.setDescription(list.getDescription());
		duplicateMailingList.setMailingListContact(new ArrayList<ContactListContact>());
		duplicateMailingList = create(actor, owner, duplicateMailingList);
		for (ContactListContact contact : list.getMailingListContact()) {
			ContactListContact duplicateContact = new ContactListContact();
			duplicateContact.setFirstName(contact.getFirstName());
			duplicateContact.setLastName(contact.getLastName());
			duplicateContact.setMail(contact.getMail());
			contactListBusinessService.addContact(duplicateMailingList, duplicateContact);
		}
		return duplicateMailingList;
	}

	@Override
	public ContactList update(Account actor, Account owner, ContactList list) throws BusinessException {
		Validate.notNull(list, "Contact list must be set.");
		Validate.notEmpty(list.getUuid(), "Contact list uuid must be set.");
		ContactList listToUpdate = find(actor, owner, list.getUuid());
		MailingListAuditLogEntry log = new MailingListAuditLogEntry(new AccountMto(actor),
				new AccountMto(listToUpdate.getOwner()), LogAction.UPDATE, AuditLogEntryType.CONTACTS_LISTS,
				listToUpdate);
		checkUpdatePermission(actor, owner, ContactList.class, BusinessErrorCode.FORBIDDEN, listToUpdate);
		if (actor.hasSuperAdminRole()) {
			// only super admin is authorized to modify list owner.
			User listOwner = list.getOwner();
			if (listOwner != null) {
				listOwner = userService.findByLsUuid(listOwner.getLsUuid());
				listToUpdate.setNewOwner(listOwner);
			}
		}
		listToUpdate = contactListBusinessService.update(listToUpdate, list);
		log.setResourceUpdated(new MailingListMto(listToUpdate));
		logEntryService.insert(log);
		return listToUpdate;
	}

	@Override
	public ContactList delete(Account actor, Account owner, String uuid) throws BusinessException {
		Validate.notNull(uuid, "Contact list must be set.");
		Validate.notEmpty(uuid, "Contact list uuid must be set.");

		ContactList listToDelete = find(actor, owner, uuid);
		checkDeletePermission(actor, owner, ContactList.class, BusinessErrorCode.FORBIDDEN, listToDelete);
		contactListBusinessService.delete(listToDelete);
		MailingListAuditLogEntry log = new MailingListAuditLogEntry(new AccountMto(actor),
				new AccountMto(listToDelete.getOwner()), LogAction.DELETE, AuditLogEntryType.CONTACTS_LISTS,
				listToDelete);
		logEntryService.insert(log);
		return listToDelete;
	}

	@Override
	public ContactListContact addContact(Account actor, Account owner, String listUuid, ContactListContact contact)
			throws BusinessException {
		Validate.notNull(contact, "Contact list must be set.");
		Validate.notEmpty(listUuid, "Mailing list uuid must be set.");

		ContactList listToUpdate = find(actor, owner, listUuid);
		checkUpdatePermission(actor, owner, ContactList.class, BusinessErrorCode.FORBIDDEN, listToUpdate);
		contact = contactListBusinessService.addContact(listToUpdate, contact);
		MailingListContactAuditLogEntry log = new MailingListContactAuditLogEntry(new AccountMto(actor),
				new AccountMto(listToUpdate.getOwner()), LogAction.CREATE, AuditLogEntryType.CONTACTS_LISTS_CONTACTS,
				listToUpdate, contact);
		logEntryService.insert(log);
		return contact;
	}

	@Override
	public void updateContact(Account actor, Account owner, ContactListContact contact) throws BusinessException {
		preChecks(actor, owner);
		Validate.notNull(contact, "Contact must be set.");
		Validate.notEmpty(contact.getUuid(), "Contact uuid must be set.");

		ContactListContact contactToUpdate = contactListBusinessService.findContact(contact.getUuid());
		ContactList list = contactToUpdate.getMailingList();
		MailingListContactAuditLogEntry log = new MailingListContactAuditLogEntry(new AccountMto(actor),
				new AccountMto(list.getOwner()), LogAction.UPDATE, AuditLogEntryType.CONTACTS_LISTS_CONTACTS, list,
				contactToUpdate);
		checkUpdatePermission(actor, owner, ContactList.class, BusinessErrorCode.FORBIDDEN, list);
		contactListBusinessService.updateContact(contact);
		contactToUpdate = contactListBusinessService.findContact(contactToUpdate.getUuid());
		log.setResourceUpdated(new MailingListContactMto(contactToUpdate));
		logEntryService.insert(log);
	}

	@Override
	public void deleteContact(Account actor, Account owner, String contactUuid) throws BusinessException {
		preChecks(actor, owner);
		Validate.notEmpty(contactUuid, "Contact uuid must be set.");

		ContactListContact contactToDelete = contactListBusinessService.findContact(contactUuid);
		ContactList list = contactToDelete.getMailingList();
		checkUpdatePermission(actor, owner, ContactList.class, BusinessErrorCode.FORBIDDEN, list);
		contactListBusinessService.deleteContact(list, contactToDelete.getUuid());
		MailingListContactAuditLogEntry log = new MailingListContactAuditLogEntry(new AccountMto(actor),
				new AccountMto(list.getOwner()), LogAction.DELETE, AuditLogEntryType.CONTACTS_LISTS_CONTACTS, list,
				contactToDelete);
		logEntryService.insert(log);
	}

	@Override
	public List<ContactListContact> findAllContacts(Account actor, Account owner, String listUuid)
			throws BusinessException {
		ContactList list = find(actor, owner, listUuid);
		return contactListBusinessService.findAllContacts(list);
	}

	@Override
	public List<ContactList> findAll(Account actor, User owner, Boolean mine) {
		List<ContactList> all = null;
		checkListPermission(actor, owner, ContactList.class, BusinessErrorCode.FORBIDDEN, null);
		if (mine == null) {
			all = contactListBusinessService.findAll(actor, owner);
		} else if (mine) {
			all = contactListBusinessService.findAllMine(actor, owner);
		} else {
			all = contactListBusinessService.findAllOthers(actor, owner);
		}
		return all;
	}

	@Override
	public List<ContactList> findAllByMemberEmail(Account actor, User owner, Boolean mine, String email) {
		Validate.notEmpty(email, "mail must be set.");
		List<ContactList> all = null;
		checkListPermission(actor, owner, ContactList.class, BusinessErrorCode.FORBIDDEN, null);
		if (mine == null) {
			all = contactListBusinessService.findAllByMemberEmail(actor, owner, email);
		} else if (mine) {
			all = contactListBusinessService.findAllMineByMemberEmail(actor, owner, email);
		} else {
			all = contactListBusinessService.findAllOthersByMemberEmail(actor, owner, email);
		}
		return all;
	}
}
