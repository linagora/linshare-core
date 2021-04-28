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

package org.linagora.linshare.core.domain.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.facade.webservice.common.dto.MailingListContactDto;
import org.linagora.linshare.core.facade.webservice.common.dto.MailingListDto;
import org.linagora.linshare.core.repository.MailingListContactRepository;

public class ContactList {

	/**
	 * Database persistence identifier
	 */
	private long persistenceId;

	/**
	 * Application identifier
	 */
	private String uuid;

	/**
	 * User identifier
	 */
	private String identifier;

	private String description;

	/**
	 * Visibility : could be private (visible by the owner only) or public.
	 */
	private boolean isPublic;

	private User owner;

	private AbstractDomain domain;

	// List of contacts.
	private List<ContactListContact> mailingListContact = new ArrayList<ContactListContact>();

	protected Date creationDate;

	protected Date modificationDate;

	public ContactList() {
		super();
	}

	public ContactList(MailingListDto list) {
		this.uuid = list.getUuid();
		this.identifier = list.getIdentifier();
		this.description = list.getDescription();
		this.isPublic = list.isPublic();
		for (MailingListContactDto current : list.getContacts()) {
			mailingListContact.add(new ContactListContact(current));
		}
	}

	public long getPersistenceId() {
		return persistenceId;
	}

	public void setPersistenceId(long persistenceId) {
		this.persistenceId = persistenceId;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public String visibility(boolean isPublic) {
		if (isPublic == true) {
			return "Public";
		} else {
			return "Private";
		}
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public AbstractDomain getDomain() {
		return domain;
	}

	public void setDomain(AbstractDomain domain) {
		this.domain = domain;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * Avoid use getter (when adding a contact to a list, mailing_list_contact_index database property (see list-index in ContactList.hbm) may always be 0, so it breaks the returned list semantic  
	 * instead of it use the {@link MailingListContactRepository} to get
	 * the contacts of the given list
	 * 
	 * @return
	 */
	public List<ContactListContact> getMailingListContact() {
		return mailingListContact;
	}

	public void setMailingListContact(List<ContactListContact> contactListMember) {
		this.mailingListContact = contactListMember;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	/**
	 * Helpers.
	 */

	/**
	 * Add a new member to the list contacts.
	 * 
	 * @param contact
	 */
	public void addMailingListContact(ContactListContact contact) {
		mailingListContact.add(contact);
	}

	/**
	 * remove a meber from the contact list .
	 * 
	 * @param contact
	 */
	public void deleteMailingListContact(ContactListContact contact) {
		mailingListContact.remove(contact);
	}

	/**
	 * Check if the actor parameter is the owner of the contact list.
	 * 
	 * @param account
	 * @return MailingList
	 */
	public boolean isOwner(Account account) {
		if (account.getLsUuid().equals(getOwner().getLsUuid())) {
			return true;
		}
		return false;
	}

	/**
	 * Set a new owner to the current list
	 * 
	 * @param owner
	 */
	public void setNewOwner(User owner) {
		setOwner(owner);
		setDomain(owner.getDomain());
	}

	/*
	 * SetBusiness
	 */

	public void setBusinessIdentifier(String identifier) {
		if (identifier != null) {
			this.identifier = identifier;
		}
	}

	public void setBusinessDescription(String description) {
		if (description != null) {
			this.description = description;
		}
	}

	public void setBusinessDomain(AbstractDomain domain) {
		if (domain != null) {
			this.domain = domain;
		}
	}

	public void setBusinessOwner(User owner) {
		if (owner != null) {
			this.owner = owner;
		}
	}
}
