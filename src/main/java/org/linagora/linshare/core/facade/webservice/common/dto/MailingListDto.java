/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2018 LINAGORA
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
package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ContactListContact;

import com.google.common.base.Function;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "MailingList")
@Schema(name = "MailingList", description = "Mailing list")
public class MailingListDto {

	@Schema(description = "Identifier")
	private String identifier;

	@Schema(description = "Description")
	private String description;

	@Schema(description = "IsPublic")
	private boolean isPublic;

	@Schema(description = "Owner")
	private UserDto owner;

	@Schema(description = "Contacts")
	private List<MailingListContactDto> contacts = new ArrayList<MailingListContactDto>();

	@Schema(description = "Uuid")
	private String uuid;

	@Schema(description = "Domain id/uuid")
	private String domainId;

	public MailingListDto() {
		super();
	}

	public MailingListDto(ContactList list) {
		this(list, true);
	}

	public MailingListDto(ContactList list, boolean full) {
		this.uuid = list.getUuid();
		this.identifier = list.getIdentifier();
		this.description = list.getDescription();
		this.isPublic = list.isPublic();
		this.owner = UserDto.getSimple(list.getOwner());
		this.domainId = list.getDomain().getUuid();
		if (full) {
			for (ContactListContact current : list.getMailingListContact()) {
				contacts.add(new MailingListContactDto(current));
			}
		}
	}

	public ContactList toObject() {
		ContactList list = new ContactList();
		list.setUuid(getUuid());
		list.setIdentifier(getIdentifier());
		list.setDescription(getDescription());
		list.setPublic(isPublic());
		for (MailingListContactDto current : getContacts()) {
			list.getMailingListContact().add(new ContactListContact(current));
		}
		return list;
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

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public UserDto getOwner() {
		return owner;
	}

	public void setOwner(UserDto owner) {
		this.owner = owner;
	}

	public List<MailingListContactDto> getContacts() {
		return contacts;
	}

	public void setContacts(List<MailingListContactDto> contacts) {
		this.contacts = contacts;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getDomainId() {
		return domainId;
	}

	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}

	/*
	 * Transformers
	 */
	public static Function<ContactList, MailingListDto> toDto() {
		return new Function<ContactList, MailingListDto>() {
			@Override
			public MailingListDto apply(ContactList arg0) {
				MailingListDto dto = new MailingListDto(arg0);
				return dto;
			}
		};
	}
}
