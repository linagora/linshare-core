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

import org.linagora.linshare.core.domain.entities.ContactList;

import com.google.common.base.Function;

@XmlRootElement(name = "ListAutoCompleteResult")
public class ListAutoCompleteResultDto extends AutoCompleteResultDto {

	private String ownerLastName;

	private String ownerFirstName;

	private String ownerMail;

	private String listName;

	public ListAutoCompleteResultDto() {
	}

	public ListAutoCompleteResultDto(ContactList list) {
		super(list.getUuid(), list.getIdentifier());
		this.ownerFirstName = list.getOwner().getFirstName();
		this.ownerLastName = list.getOwner().getLastName();
		this.ownerMail = list.getOwner().getMail();
		this.listName = list.getIdentifier();
	}

	public String getOwnerLastName() {
		return ownerLastName;
	}

	public void setOwnerLastName(String ownerLastName) {
		this.ownerLastName = ownerLastName;
	}

	public String getOwnerFirstName() {
		return ownerFirstName;
	}

	public void setOwnerFirstName(String ownerFirstName) {
		this.ownerFirstName = ownerFirstName;
	}

	public String getOwnerMail() {
		return ownerMail;
	}

	public void setOwnerMail(String ownerMail) {
		this.ownerMail = ownerMail;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}

	public static Function<ContactList, ListAutoCompleteResultDto> toDto() {
		return new Function<ContactList, ListAutoCompleteResultDto>() {
			@Override
			public ListAutoCompleteResultDto apply(ContactList arg0) {
				return new ListAutoCompleteResultDto(arg0);
			}
		};
	}
}