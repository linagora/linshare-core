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
package org.linagora.linshare.core.domain.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.linagora.linshare.core.facade.webservice.common.dto.MailingListContactDto;
import org.linagora.linshare.core.facade.webservice.common.dto.MailingListDto;

public class ContactList implements Serializable {

	private static final long serialVersionUID = -1424966505036226433L;
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
	private Set<ContactListContact> contactListContacts = new HashSet<ContactListContact>();

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
			contactListContacts.add(new ContactListContact(current));
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


	public Set<ContactListContact> getContactListContacts() {
		return contactListContacts;
	}

	public void setContactListContacts(Set<ContactListContact> contactListContacts) {
		this.contactListContacts = contactListContacts;
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
		contactListContacts.add(contact);
	}

	/**
	 * remove a meber from the contact list .
	 * 
	 * @param contact
	 */
	public void deleteMailingListContact(ContactListContact contact) {
		contactListContacts.remove(contact);
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
