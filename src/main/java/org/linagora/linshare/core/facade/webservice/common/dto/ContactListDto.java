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

import org.linagora.linshare.core.domain.entities.ContactList;

import com.google.common.base.Function;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "ContactList")
@Schema(name = "ContactList", description = "Contact list")
public class ContactListDto {

	@Schema(description = "Name")
	private String name;

	@Schema(description = "Description")
	private String description;

	@Schema(description = "IsPublic")
	private boolean isPublic;

	@Schema(description = "Owner")
	private GenericUserDto owner;

	@Schema(description = "Uuid")
	private String uuid;

	@Schema(description = "Domain")
	private DomainLightDto domain;

	@Schema(description = "Creation Date")
	protected Date creationDate;

	@Schema(description = "Modification Date")
	protected Date modificationDate;

	public ContactListDto() {
		super();
	}

	public ContactListDto(ContactList list) {
		this.uuid = list.getUuid();
		this.name = list.getIdentifier();
		this.description = list.getDescription();
		this.isPublic = list.isPublic();
		this.owner = new GenericUserDto(list.getOwner());
		this.domain = new DomainLightDto(list.getDomain());
		this.creationDate = list.getCreationDate();
		this.modificationDate = list.getModificationDate();
	}

	public ContactList toObject() {
		ContactList list = new ContactList();
		list.setUuid(getUuid());
		list.setIdentifier(getName());
		list.setDescription(getDescription());
		list.setPublic(isPublic());
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

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public GenericUserDto getOwner() {
		return owner;
	}

	public void setOwner(GenericUserDto owner) {
		this.owner = owner;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public DomainLightDto getDomain() {
		return domain;
	}

	public void setDomain(DomainLightDto domain) {
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

	/*
	 * Transformers
	 */

	public static Function<ContactList, ContactListDto> toDto() {
		return new Function<ContactList, ContactListDto>() {
			@Override
			public ContactListDto apply(ContactList arg0) {
				return new ContactListDto(arg0);
			}
		};
	}
}
