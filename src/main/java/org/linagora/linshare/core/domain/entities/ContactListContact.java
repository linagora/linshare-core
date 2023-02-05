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

import java.util.Date;

import org.linagora.linshare.core.facade.webservice.common.dto.MailingListContactDto;

public class ContactListContact {

	private long persistenceId;

	private String uuid;

	private String mail;

	private String firstName;
	
	private String lastName;

	protected Date creationDate;

	protected Date modificationDate;
	
	private ContactList contactList;
	/**
	 * Constructors
	 */
	
	/**
	 * Hibernate constructor.
	 */
	public ContactListContact() {
	}

	public ContactListContact(String mail, String firstName, String lastName) {
		this.mail = mail;
		this.setLastName(lastName);
		this.setFirstName(firstName);
	}

	public ContactListContact(MailingListContactDto contactListContact)  {
		this.mail = contactListContact.getMail();
		this.uuid = contactListContact.getUuid();
		this.setLastName(contactListContact.getLastName());
		this.setFirstName(contactListContact.getFirstName());
	}

	/**
	 * Getter(s) / Setter(s)
	 */

	public long getPersistenceId() {
		return persistenceId;
	}

	public void setPersistenceId(long persistenceId) {
		this.persistenceId = persistenceId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mail == null) ? 0 : mail.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContactListContact other = (ContactListContact) obj;
		if (mail == null) {
			if (other.mail != null)
				return false;
		} else if (!mail.equals(other.mail))
			return false;
		return true;
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

	public ContactList getContactList() {
		return contactList;
	}

	public void setContactList(ContactList contactList) {
		this.contactList = contactList;
	}

	/**
	 * Helpers
	 */
	
}
