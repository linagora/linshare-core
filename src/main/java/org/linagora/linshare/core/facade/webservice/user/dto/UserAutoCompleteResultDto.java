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

import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;

import com.google.common.base.Function;

@XmlRootElement(name = "UserAutoCompleteResult")
public class UserAutoCompleteResultDto extends AutoCompleteResultDto {

	private String firstName;

	private String lastName;

	private String domain;

	private String mail;

	public UserAutoCompleteResultDto(UserDto user) {
		super(user.getUuid(), user.getMail());
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.domain = user.getDomain();
		this.mail = user.getMail();
	}

	public UserAutoCompleteResultDto(User user) {
		super(user.getLsUuid(), user.getMail());
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.domain = user.getDomain().getUuid();
		this.mail = user.getMail();
	}

	public UserAutoCompleteResultDto() {
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

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public static Function<UserDto, UserAutoCompleteResultDto> toDto() {
		return UserAutoCompleteResultDto::new;
	}

}
