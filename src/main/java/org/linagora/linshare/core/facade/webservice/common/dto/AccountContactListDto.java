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
	public AccountContactListDto() {
		super();
	}

	public AccountContactListDto(AccountContactLists list) {
		this.uuid = list.getContactList().getUuid();
		this.name = list.getContactList().getIdentifier();
		this.description = list.getContactList().getDescription();
		this.domain = new CommonDomainLightDto(list.getContactList().getDomain());
		this.creationDate = list.getContactList().getCreationDate();
		this.modificationDate = list.getContactList().getModificationDate();
		this.contactList = new ContactListDto(list.getContactList()) ;
	}

	public AccountContactLists toObject() {
		AccountContactLists list = new AccountContactLists();
		list.getContactList().setUuid(getUuid());
		list.getContactList().setIdentifier(getName());
		list.getContactList().setDescription(getDescription());
		return list;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public CommonDomainLightDto getDomain() {
		return domain;
	}

	public void setDomain(CommonDomainLightDto domain) {
		this.domain = domain;
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

	public AccountDto getAccount() {
		return account;
	}

	public void setAccount(AccountDto account) {
		this.account = account;
	}

	public ContactListDto getContactList() {
		return contactList;
	}

	public void setContactList(ContactListDto contactList) {
		this.contactList = contactList;
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
