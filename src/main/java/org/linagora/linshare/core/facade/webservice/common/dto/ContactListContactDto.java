/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2016. Contribute to
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
package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.MailingListContact;

import com.google.common.base.Function;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "ContactListContact")
@ApiModel(value = "ContactListContact", description = "Contacts of contact list")
public class ContactListContactDto {

	@ApiModelProperty(value = "Mail")
	private String mail;

	@ApiModelProperty(value = "Uuid")
	private String uuid;

	@ApiModelProperty(value = "FirstName")
	private String firstName;

	@ApiModelProperty(value = "LastName")
	private String lastName;

	@ApiModelProperty(value = "MailingListUuid")
	private String mailingListUuid;

	@ApiModelProperty(value = "Creation Date")
	protected Date creationDate;

	@ApiModelProperty(value = "Modification Date")
	protected Date modificationDate;

	public ContactListContactDto() {
	}

	public ContactListContactDto(MailingListContact contact) {
		this.mail = contact.getMail();
		this.uuid = contact.getUuid();
		this.lastName = contact.getLastName();
		this.firstName = contact.getFirstName();
		this.mailingListUuid = contact.getMailingList().getUuid();
		this.creationDate = contact.getCreationDate();
		this.modificationDate = contact.getModificationDate();
	}

	public MailingListContact toObject() {
		MailingListContact contact = new MailingListContact();
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
	public static Function<MailingListContact, ContactListContactDto> toDto() {
		return new Function<MailingListContact, ContactListContactDto>() {
			@Override
			public ContactListContactDto apply(MailingListContact arg0) {
				return new ContactListContactDto(arg0);
			}
		};
	}
}

