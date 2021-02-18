/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
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