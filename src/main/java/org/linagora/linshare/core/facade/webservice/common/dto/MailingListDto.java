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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ContactListContact;

import com.google.common.base.Function;
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

	@Schema(description = "Domain label")
	private String domainLabel;

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
		this.domainLabel = list.getDomain().getLabel();
		if (full) {
			for (ContactListContact current : list.getContactListContacts()) {
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
			list.getContactListContacts().add(new ContactListContact(current));
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

	public String getDomainLabel() {
		return domainLabel;
	}

	public void setDomainLabel(String domainLabel) {
		this.domainLabel = domainLabel;
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
