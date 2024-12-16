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
package org.linagora.linshare.core.business.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountContactLists;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ContactListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.exception.BusinessException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

	ContactListContact updateContact(ContactListContact contactToUpdate) throws BusinessException;

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

	List<ContactList> findAllListManagedByUser(User user);

	void transferContactListFromGuestToInternal(@Nonnull final Guest guest,@Nonnull final Account authUser);

	/**
	 * <p>Retrieve a set of contact lists from their UUIDs validating their access permissions against
	 * the given account and optionally a guest's moderators.</p>
	 * <p>This method ensures that only valid contact lists, either public within the actor's domain
	 * or private owned by the actor, are added to the returned contact lists.If a guest is provided,
	 * the method verifies that moderators linked to the guest are taken into account for the validation.</p>
	 *
	 * @param actor                The {@link Account} used to verify domain and ownership permissions.
	 *                             Must not be {@code null}.
	 * @param guest                An optional {@link Guest} whose moderators are considered for additional
	 *                             validation rules. May be {@code null}.
	 * @param contactListUuids     A {@link List} of UUIDs representing the contact lists
	 *                             to be retrieved and validated. May be {@code null} or empty.
	 * @return                     A {@link List} of {@link ContactList} entities that have passed
	 *                             the validation checks. Not {@code null}.
	 * @throws BusinessException   If a public contact list belongs to another domain, or if the actor
	 *                             attempts to use private contact lists they do not own.
	 */
	public @Nonnull List<ContactList> findByAccountAndContactListUuids(@Nonnull final Account actor, @Nullable final Guest guest, @Nonnull final List<String> contactListUuids);

	/**
	 * Updates the contact lists for a guest, ensuring consistency based on the guest's
	 * restricted contact status.
	 *
	 * <p>Scenarios handled:
	 * <ul>
	 *   <li>If the status hasn't changed, it synchronizes contact lists by adding new ones
	 *       and removing obsolete ones.</li>
	 *   <li>If the guest was restricted but is no longer, all contact lists are purged.</li>
	 *   <li>If the guest becomes restricted without contact lists, a {@link BusinessException} is thrown.</li>
	 * </ul>
	 *
	 * @param update The guest being updated.
	 * @param contactLists The new list of allowed contacts, if applicable.
	 * @throws BusinessException If a restricted guest is updated or created without contact lists.
	 */
	void updateAccountContactLists(@Nonnull Guest update, @Nonnull List<ContactList> contactLists);

	/**
	 * Finds and retrieves the list of {@link AccountContactLists} associated with a
	 * given {@link Account}
	 * @param account           the {@link Account} entity
	 * @return                A {@link List} of {@link AccountContactLists}
	 */
	public @Nonnull List<AccountContactLists> findAccountContactListByAccount(@Nonnull final Account account);

}
