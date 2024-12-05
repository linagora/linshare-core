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
package org.linagora.linshare.core.facade.webservice.user.dto;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.RecipientFavourite;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;
import io.swagger.v3.oas.annotations.media.Schema;
import org.linagora.linshare.core.facade.webservice.common.dto.GenericUserDto;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@Type(value = AutoCompleteResultDto.class, name = "simple"),
		@Type(value = UserAutoCompleteResultDto.class, name = "user"),
		@Type(value = ThreadMemberAutoCompleteResultDto.class, name = "threadmember"),
		@Type(value = WorkgroupMemberAutoCompleteResultDto.class, name = "sharedspace_member"),
		@Type(value = ListAutoCompleteResultDto.class, name = "mailinglist"), })
@XmlRootElement(name = "AutoCompleteResult")
@XmlSeeAlso({ UserAutoCompleteResultDto.class,
		ThreadMemberAutoCompleteResultDto.class,
		WorkgroupMemberAutoCompleteResultDto.class,
		ListAutoCompleteResultDto.class })
@Schema(name = "AutoCompleteResult", description = "Auto complete result object")
public class AutoCompleteResultDto {

	private String identifier;

	private String display;

	@Schema(description = "IsPublic")
	private boolean isPublic;

	@Schema(description = "Owner")
	private GenericUserDto owner;

	@Schema(description = "Uuid")
	private String uuid;

	@Schema(description = "Creation Date")
	protected Date creationDate;

	@Schema(description = "Modification Date")
	protected Date modificationDate;

	public AutoCompleteResultDto() {
		super();
	}

	public AutoCompleteResultDto(String identifier, String display) {
		this.identifier = identifier;
		this.display = display;
	}

	public AutoCompleteResultDto(ContactList contactList) {
		this.identifier = contactList.getUuid();
		this.display = contactList.getIdentifier();
		this.isPublic = contactList.isPublic();
		this.owner = new GenericUserDto(contactList.getOwner());
		this.creationDate = contactList.getCreationDate();
		this.modificationDate = contactList.getModificationDate();
	}

	public AutoCompleteResultDto(RecipientFavourite recipientFavourite) {
		identifier = recipientFavourite.getRecipient();
		display = recipientFavourite.getRecipient();
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean aPublic) {
		isPublic = aPublic;
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

	public static Function<RecipientFavourite, AutoCompleteResultDto> toRFDto() {
		return new Function<RecipientFavourite, AutoCompleteResultDto>() {
			@Override
			public AutoCompleteResultDto apply(RecipientFavourite arg0) {
				return new AutoCompleteResultDto(arg0);
			}
		};
	}

}
