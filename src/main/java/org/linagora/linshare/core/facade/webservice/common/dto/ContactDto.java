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

import org.apache.commons.lang3.StringUtils;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.User;

import io.swagger.v3.oas.annotations.media.Schema;

public class ContactDto {

	@Schema(description = "FirstName")
	private String firstName;

	@Schema(description = "LastName")
	private String lastName;

	@Schema(description = "Mail")
	private String mail;

	public ContactDto() {
	}

	public ContactDto(String firstName, String lastName, String mail) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.mail = mail;
	}

	public ContactDto(Account account) {
		super();
		User u = (User)account;
		this.firstName = u.getFirstName();
		this.lastName = u.getLastName();
		this.mail = u.getMail();
	}

	public ContactDto(Contact contact) {
		super();
		// TODO support first and last name in contact ?
		this.firstName = null;
		this.lastName = null;
		this.mail = contact.getMail();
	}

	@Override
	public String toString() {
		StringBuilder res = new StringBuilder("Contact : ");
		if (!StringUtils.isBlank(firstName)
				&& !StringUtils.isBlank(this.lastName)) {
			res.append(this.firstName);
			res.append(" ");
			res.append(this.lastName);
			res.append(" : ");
		}
		res.append(this.mail);
		return res.toString();
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

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

}
