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
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
package org.linagora.linshare.core.business.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ContactListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface MailingListBusinessService {

	/**
	 * Mailing list management.
	 */

	ContactList createList(ContactList contactList, User owner) throws BusinessException;

	/**
	 * Find a mailing list by its uuid.
	 * 
	 * @param uuid
	 * @return MailingList
	 * @throws BusinessException
	 *             if not found.
	 */
	ContactList findByUuid(String uuid) throws BusinessException;

	List<ContactList> findAllList();

	/**
	 * Find all list of the selected user (private and public)
	 * 
	 * @param user
	 * @return List<MailingList>
	 */
	List<ContactList> findAllListByUser(User user);

	void deleteList(String uuid) throws BusinessException;

	ContactList updateList(ContactList contactList) throws BusinessException;

	/**
	 * Find all list where user is owner
	 * 
	 * @param user
	 * @return List<MailingList>
	 */
	List<ContactList> findAllMyList(User user);

	public ContactList findByIdentifier(User owner, String identifier);

	/**
	 * Find all my list according to select visibility
	 * 
	 * @param owner
	 * @param isPublic
	 * @return List<MailingList>
	 */
	List<ContactList> findAllListByVisibility(User owner, boolean isPublic);

	/**
	 * Find All list according to pattern where user is owner
	 * 
	 * @param user
	 * @param input
	 * @return List<MailingList>
	 */
	List<ContactList> searchMyLists(User user, String input);

	/**
	 * Find all user list according to pattern
	 * 
	 * @param user
	 * @param input
	 * @return List<MailingList>
	 */
	List<ContactList> searchListByUser(User user, String input);

	/**
	 * Find all user list according to selected visibility and input
	 * 
	 * @param owner
	 * @param isPublic
	 * @param input
	 * @return List<MailingList>
	 */
	List<ContactList> searchListByVisibility(User owner, boolean isPublic, String input);

	/**
	 * Mailing listContact management.
	 */

	void deleteContact(ContactList contactList, String mail) throws BusinessException;

	/**
	 * Add contact to list
	 * 
	 * @param contactList
	 * @param contact
	 * @return MailingListContact
	 * @throws BusinessException
	 */
	ContactListContact addContact(ContactList contactList, ContactListContact contact) throws BusinessException;

	ContactListContact findContact(String contactUuid) throws BusinessException;

	void updateContact(ContactListContact contactToUpdate) throws BusinessException;

	ContactListContact findContactWithMail(String listUuid, String mail) throws BusinessException;

	List<String> getAllContactMails(ContactList list);

	List<ContactListContact> findAllContacts(ContactList list) throws BusinessException;

	/*
	 * Webservices methods.
	 */

	ContactList update(ContactList entity, ContactList object) throws BusinessException;

	ContactList delete(ContactList entity) throws BusinessException;

	List<ContactList> findAll(Account actor, User user);

	List<ContactList> findAllMine(Account actor, User user);

	List<ContactList> findAllOthers(Account actor, User user);
	
	List<ContactList> findAllByMemberEmail(Account actor, User user, String email);

	List<ContactList> findAllMineByMemberEmail(Account actor, User user, String email);

	List<ContactList> findAllOthersByMemberEmail(Account actor, User user, String email);
}
