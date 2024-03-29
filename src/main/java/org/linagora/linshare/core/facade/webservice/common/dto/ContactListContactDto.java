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
package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.ContactListContact;

import com.google.common.base.Function;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "ContactListContact")
@Schema(name = "ContactListContact", description = "Contacts of contact list")
public class ContactListContactDto {

	@Schema(description = "Mail")
	private String mail;

	@Schema(description = "Uuid")
	private String uuid;

	@Schema(description = "FirstName")
	private String firstName;

	@Schema(description = "LastName")
	private String lastName;

	@Schema(description = "MailingListUuid")
	private String mailingListUuid;

	@Schema(description = "Creation Date")
	protected Date creationDate;

	@Schema(description = "Modification Date")
	protected Date modificationDate;

	public ContactListContactDto() {
	}

	public ContactListContactDto(ContactListContact contact) {
		this.mail = contact.getMail();
		this.uuid = contact.getUuid();
		this.lastName = contact.getLastName();
		this.firstName = contact.getFirstName();
		this.mailingListUuid = contact.getContactList().getUuid();
		this.creationDate = contact.getCreationDate();
		this.modificationDate = contact.getModificationDate();
	}

	public ContactListContact toObject() {
		ContactListContact contact = new ContactListContact();
		contact.setUuid(getUuid());
		contact.setMail(getMail());
		contact.setFirstName(getFirstName());
		contact.setLastName(getLastName());
		return contact;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public String getMailingListUuid() {
		return mailingListUuid;
	}

	public void setMailingListUuid(String mailingListUuid) {
		this.mailingListUuid = mailingListUuid;
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

	/*
	 * Transformers
	 */
	public static Function<ContactListContact, ContactListContactDto> toDto() {
		return new Function<ContactListContact, ContactListContactDto>() {
			@Override
			public ContactListContactDto apply(ContactListContact arg0) {
				return new ContactListContactDto(arg0);
			}
		};
	}
}

