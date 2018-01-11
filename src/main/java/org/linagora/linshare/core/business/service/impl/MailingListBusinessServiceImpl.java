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

package org.linagora.linshare.core.business.service.impl;

import java.util.List;

import org.linagora.linshare.core.business.service.MailingListBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ContactListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.MailingListContactRepository;
import org.linagora.linshare.core.repository.MailingListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailingListBusinessServiceImpl implements MailingListBusinessService {

	private static final Logger logger = LoggerFactory.getLogger(MailingListBusinessServiceImpl.class);
	private final MailingListRepository listRepository;
	private final MailingListContactRepository contactRepository;
	
	public MailingListBusinessServiceImpl(MailingListRepository mailingListRepository,
			MailingListContactRepository mailingListContactRepository) {
		super();
		this.listRepository = mailingListRepository;
		this.contactRepository = mailingListContactRepository;
	}

	/**
	 * Mailing list management.
	 */

	@Override
	public ContactList createList(ContactList contactList, User owner) throws BusinessException {
		// check if list identifier is unique (do not already exist)
		if (listRepository.findByIdentifier(owner, contactList.getIdentifier()) == null) {
			// Setting extra fields.
			contactList.setOwner(owner);
			contactList.setDomain(owner.getDomain());
			ContactList createdList = listRepository.create(contactList);

			return createdList;
		} else {
			String msg = "The current list you are trying to create already exists : " + contactList.getIdentifier();
			logger.error(msg);
			throw new BusinessException(BusinessErrorCode.LIST_ALDREADY_EXISTS, msg);
		}
	}

	@Override
	public ContactList findByUuid(String uuid) throws BusinessException {
		ContactList contactList = listRepository.findByUuid(uuid);
		if (contactList == null) {
			String msg = "The current mailing list do not exist : " + uuid;
			logger.error(msg);
			throw new BusinessException(BusinessErrorCode.LIST_DO_NOT_EXIST, msg);
		}
		return contactList;
	}

	@Override
	public List<ContactList> findAllList() {
		return listRepository.findAll();
	}

	@Override
	public ContactList findByIdentifier(User owner, String identifier) {
		return listRepository.findByIdentifier(owner, identifier);
	}

	@Override
	public List<String> getAllContactMails(ContactList list) {
		return contactRepository.getAllContactMails(list);
	}

	@Override
	public List<ContactList> searchListByVisibility(User owner, boolean isPublic, String input) {
		return listRepository.searchWithInputByVisibility(owner, isPublic, input);
	}

	@Override
	public List<ContactList> findAllListByVisibility(User owner, boolean isPublic) {
		return listRepository.searchListByVisibility(owner, isPublic);
	}

	@Override
	public List<ContactList> searchMyLists(User user, String input) {
		return listRepository.searchMyListWithInput(user, input);
	}

	@Override
	public List<ContactList> searchListByUser(User user, String input) {
		return listRepository.searchListWithInput(user, input);
	}

	@Override
	public List<ContactList> findAllListByUser(User user) {
		return listRepository.findAllMyList(user);
	}

	@Override
	public List<ContactList> findAllMyList(User user) {
		return listRepository.findAllListWhereOwner(user);
	}

	@Override
	public void deleteList(String uuid) throws BusinessException {
		ContactList listToDelete = findByUuid(uuid);
		logger.debug("List to delete: " + uuid);
		listRepository.delete(listToDelete);
	}

	@Override
	public ContactList updateList(ContactList updatedMailingList) throws BusinessException {
		ContactList entity = findByUuid(updatedMailingList.getUuid());
		String newIdentifier = updatedMailingList.getIdentifier();
		if (!entity.getIdentifier().equals(newIdentifier)) {
			// The identifier was changed.
			// check if new list identifier is unique (do not already exist)
			if (listRepository.findByIdentifier(entity.getOwner(), newIdentifier) != null) {
				String msg = "Update failed : current list identifier  already exists : " + newIdentifier;
				logger.error(msg);
				throw new BusinessException(BusinessErrorCode.LIST_ALDREADY_EXISTS, msg);
			}
		}
		entity.setIdentifier(newIdentifier);
		entity.setDescription(updatedMailingList.getDescription());
		entity.setPublic(updatedMailingList.isPublic());
		if (updatedMailingList.getDomain() != null)
			entity.setDomain(updatedMailingList.getDomain());
		if (updatedMailingList.getOwner() != null)
			entity.setOwner(updatedMailingList.getOwner());
		return listRepository.update(entity);
	}

	/**
	 * Mailing list contacts management.
	 */

	@Override
	public ContactListContact findContactWithMail(String listUuid, String mail) throws BusinessException {
		ContactList list = listRepository.findByUuid(listUuid);
		ContactListContact contact = contactRepository.findByMail(list, mail);
		if (contact == null) {
			String msg = "The current contact you are trying to find do not exist : " + mail;
			logger.error(msg);
			throw new BusinessException(BusinessErrorCode.CONTACT_LIST_DO_NOT_EXIST, msg);
		}
		return contact;
	}

	@Override
	public ContactListContact findContact(String contactUuid) throws BusinessException {
		ContactListContact contact = contactRepository.findByUuid(contactUuid);
		if (contact == null) {
			String msg = "The current contact you are trying to find do not exist : " + contactUuid;
			logger.error(msg);
			throw new BusinessException(BusinessErrorCode.CONTACT_LIST_DO_NOT_EXIST, msg);
		}
		return contact;
	}

	@Override
	public void deleteContact(ContactList contactList, String contactUuid) throws BusinessException {
		ContactListContact contactToDelete = findContact(contactUuid);
		contactList.deleteMailingListContact(contactToDelete);
		listRepository.update(contactList);
		contactRepository.delete(contactToDelete);
	}

	@Override
	public void updateContact(ContactListContact contactToUpdate)
			throws BusinessException {
		/*
		 * FIXME : update can leads to duplicate mails which in turn will break
		 * the deletion
		 */
		ContactListContact contact = findContact(contactToUpdate.getUuid());
		contact.setMail(contactToUpdate.getMail());
		contact.setLastName(contactToUpdate.getLastName());
		contact.setFirstName(contactToUpdate.getFirstName());
		contactRepository.update(contact);
	}

	@Override
	public ContactListContact addContact(ContactList contactList, ContactListContact contact) throws BusinessException {
		List<ContactListContact> contactListContact = findAllContacts(contactList);
		if (!contactListContact.contains(contact)) {
			contactList.addMailingListContact(contact);
			contact.setMailingList(contactList);
			contact = contactRepository.create(contact);
			listRepository.update(contactList);
			return contact;
		} else {
			logger.debug("Contact already present : " + contact.getMail());
			throw new BusinessException(BusinessErrorCode.MAILING_LIST_CONTACT_ALREADY_EXISTS, "Contact already exists.");
		}
	}

	@Override
	public List<ContactListContact> findAllContacts(ContactList list) {
		return contactRepository.findAllContacts(list);
	}

	/*
	 * Webservices methods
	 */

	@Override
	public ContactList update(ContactList entity, ContactList object) throws BusinessException {
		String newIdentifier = object.getIdentifier();
		if (!entity.getIdentifier().equals(newIdentifier)) {
			// The identifier was changed.
			// check if new list identifier is unique (do not already exist)
			if (listRepository.findByIdentifier(entity.getOwner(), newIdentifier) != null) {
				String msg = "Update failed : current list identifier  already exists : " + newIdentifier;
				logger.error(msg);
				throw new BusinessException(BusinessErrorCode.LIST_ALDREADY_EXISTS, msg);
			}
		}
		entity.setBusinessIdentifier(newIdentifier);
		entity.setBusinessDescription(object.getDescription());
		entity.setPublic(object.isPublic());
		return listRepository.update(entity);
	}

	@Override
	public ContactList delete(ContactList entity) throws BusinessException {
		logger.debug("List to delete: " + entity.getUuid());
		listRepository.delete(entity);
		return entity;
	}

	@Override
	public List<ContactList> findAll(Account actor, User user) {
		return listRepository.findAll(user);
	}

	@Override
	public List<ContactList> findAllMine(Account actor, User user) {
		return listRepository.findAllMine(user);
	}

	@Override
	public List<ContactList> findAllOthers(Account actor, User user) {
		return listRepository.findAllOthers(user);
	}

	@Override
	public List<ContactList> findAllByMemberEmail(Account actor, User user, String email) {
		return listRepository.findAllByMemberEmail(user, email);
	}

	@Override
	public List<ContactList> findAllMineByMemberEmail(Account actor, User user, String email) {
		return listRepository.findAllMineByMemberEmail(user, email);
	}

	@Override
	public List<ContactList> findAllOthersByMemberEmail(Account actor, User user, String email) {
		return listRepository.findAllOthersByMemberEmail(user, email);
	}
}
