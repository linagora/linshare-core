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
package org.linagora.linshare.core.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ContactListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface ContactListService {

	ContactList createList(String actorUuid, String ownerUuid, ContactList contactList) throws BusinessException;

	ContactList deleteList(String actorUuid, String contactListUuid) throws BusinessException;

	ContactList updateList(String actorUuid, ContactList listToUpdate) throws BusinessException;

	ContactList findByUuid(String actorUuid, String uuid) throws BusinessException;

	List<ContactList> findAllListManagedByUser(String userUuid);

	/**
	 * Find all list (private and public) of the selected user
	 * 
	 * @return contact list
	 */
	List<ContactList> findAllListByUser(String actorUuid, String userUuid);

	public ContactList findByIdentifier(String ownerUuid, String identifier);

	/**
	 * Find all list of the user
	 */
	List<ContactList> findAllListByOwner(String actorUuid, String ownerUuid);

	/**
	 * find list of result according to visibility selected and pattern
	 */
	List<ContactList> searchListByVisibility(String actorUuid, String criteriaOnSearch, String pattern);

	/**
	 * Basic operations on contactListMember
	 */

	/**
	 * Add contact to list
	 */
	void addNewContact(String actorUuid, String contactListUuid, ContactListContact contact) throws BusinessException;

	void deleteContact(String actorUuid, String mail) throws BusinessException;

	ContactListContact searchContact(String actorUuid, String uuid) throws BusinessException;

	void updateContact(String actorUuid, ContactListContact contactToUpdate) throws BusinessException;

	ContactListContact findContactWithMail(String actorUuid, String listUuid, String mail) throws BusinessException;

	List<String> getAllContactMails(String actorUuid, String uuid) throws BusinessException;

	/*
	 * Webservice methods
	 */

	List<ContactList> findAllByUser(Account actor, Account owner) throws BusinessException;

	ContactList find(Account actor, Account owner, String uuid) throws BusinessException;

	ContactList create(Account actor, Account owner, ContactList list) throws BusinessException;

	ContactList duplicate(Account actor, Account owner, ContactList list, String identifier) throws BusinessException;

	ContactList update(Account actor, Account owner, ContactList list) throws BusinessException;

	ContactList delete(Account actor, Account owner, String uuid) throws BusinessException;

	ContactListContact addContact(Account actor, Account owner, String listUuid, ContactListContact contact) throws BusinessException;

	ContactListContact updateContact(Account actor, Account owner, ContactListContact contact) throws BusinessException;

	void deleteContact(Account actor, Account owner, String contactUuid) throws BusinessException;

	List<ContactListContact> findAllContacts(Account actor, Account owner, String listUuid) throws BusinessException;

	List<ContactList> findAll(Account actor, User owner, Boolean mine);
	
	List<ContactList> findAllByMemberEmail(Account actor, User owner, Boolean mine, String email);
}