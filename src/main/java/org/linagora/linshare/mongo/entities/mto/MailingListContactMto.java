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
package org.linagora.linshare.mongo.entities.mto;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.ContactListContact;

@XmlRootElement(name = "MailingListContact")
public class MailingListContactMto {

	protected String uuid;

	protected String mail;

	protected String firstName;

	protected String lastName;

	protected String listUuid;

	public MailingListContactMto() {
	}

	public MailingListContactMto(ContactListContact contact) {
		this.uuid = contact.getUuid();
		this.mail = contact.getMail();
		this.listUuid = contact.getContactList().getUuid();
		this.firstName = contact.getFirstName();
		this.lastName = contact.getLastName();
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

	public String getListUuid() {
		return listUuid;
	}

	public void setListUuid(String listUuid) {
		this.listUuid = listUuid;
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
}
