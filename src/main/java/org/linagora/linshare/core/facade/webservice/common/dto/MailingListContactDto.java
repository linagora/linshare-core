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
package org.linagora.linshare.core.facade.webservice.common.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.ContactListContact;

import com.google.common.base.Function;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "MailingListContact")
@Schema(name = "MailingListContact", description = "Mailing list contact")
public class MailingListContactDto {

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

	public MailingListContactDto() {
	}

	public MailingListContactDto(ContactListContact contact) {
		this.mail = contact.getMail();
		this.uuid = contact.getUuid();
		this.lastName = contact.getLastName();
		this.firstName = contact.getFirstName();
		this.mailingListUuid = contact.getContactList().getUuid();
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

	/*
	 * Transformers
	 */
	public static Function<ContactListContact, MailingListContactDto> toDto() {
		return new Function<ContactListContact, MailingListContactDto>() {
			@Override
			public MailingListContactDto apply(ContactListContact arg0) {
				return new MailingListContactDto(arg0);
			}
		};
	}
}

