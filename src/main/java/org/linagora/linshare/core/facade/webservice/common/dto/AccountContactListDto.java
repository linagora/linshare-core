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

import com.google.common.base.Function;
import io.swagger.v3.oas.annotations.media.Schema;
import org.linagora.linshare.core.domain.entities.AccountContactLists;
import org.linagora.linshare.core.domain.entities.ContactList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name = "AllowedContactList")
@XmlAccessorType(XmlAccessType.FIELD)
@Schema(name = "AccountContactList", description = "Account Contact list")
public class AccountContactListDto {

	@Schema(description = "Name")
	private String name;

	@Schema(description = "Description")
	private String description;

	@Schema(description = "Uuid")
	private String uuid;

	@Schema(description = "Domain")
	private CommonDomainLightDto domain;

	@Schema(description = "Creation Date")
	protected Date creationDate;

	@Schema(description = "Modification Date")
	protected Date modificationDate;

	@Schema(description = "Contact_list")
	private ContactListDto contactList;

	private AccountDto account;

	@Schema(description = "can_view_contact_list_members")
	private Boolean canViewContactListMembers;

	public AccountContactListDto() {
		super();
	}

	public AccountContactListDto(final AccountContactLists list) {
		if (list == null) {
			return;
		}
		final ContactList contactList = list.getContactList();
		if (contactList != null) {

			this.uuid = contactList.getUuid();
			this.name = contactList.getIdentifier();
			this.description = contactList.getDescription();
			if (contactList.getDomain() != null) {
				this.domain = new CommonDomainLightDto(contactList.getDomain());
			}
			this.creationDate = contactList.getCreationDate();
			this.modificationDate = contactList.getModificationDate();
			this.contactList = new ContactListDto(contactList);
		}
		this.canViewContactListMembers = list.getCanViewContactListMembers();
	}
	public AccountContactLists toObject() {
		final AccountContactLists list = new AccountContactLists();
		final ContactList contactList = new ContactList();
		contactList.setUuid(getUuid());
		contactList.setIdentifier(getName());
		contactList.setDescription(getDescription());
		list.setContactList(contactList);
		list.setCanViewContactListMembers(getCanViewContactListMembers());
		return list;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(final String uuid) {
		this.uuid = uuid;
	}

	public CommonDomainLightDto getDomain() {
		return this.domain;
	}

	public void setDomain(final CommonDomainLightDto domain) {
		this.domain = domain;
	}

	public Date getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return this.modificationDate;
	}

	public void setModificationDate(final Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public AccountDto getAccount() {
		return this.account;
	}

	public void setAccount(final AccountDto account) {
		this.account = account;
	}

	public ContactListDto getContactList() {
		return this.contactList;
	}

	public void setContactList(final ContactListDto contactList) {
		this.contactList = contactList;
	}

	public Boolean getCanViewContactListMembers() {
		return this.canViewContactListMembers;
	}

	public void setCanViewContactListMembers(final Boolean canViewContactListMembers) {
		this.canViewContactListMembers = canViewContactListMembers;
	}
	/*
	 * Transformers
	 */

	public static Function<AccountContactLists, AccountContactListDto> toDto() {
		return new Function<AccountContactLists, AccountContactListDto>() {
			@Override
			public AccountContactListDto apply(AccountContactLists arg0) {
				return new AccountContactListDto(arg0);
			}
		};
	}
}
